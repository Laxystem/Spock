package quest.laxla.spock.example

import io.github.oshai.kotlinlogging.Level
import io.ygdrasil.webgpu.*
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quest.laxla.spock.*
import quest.laxla.spock.math.z
import quest.laxla.spock.toolkit.ShaderModuleDescriptor
import quest.laxla.spock.toolkit.Surface
import quest.laxla.spock.toolkit.WebGpuRenderer
import quest.laxla.spock.toolkit.asSize3D
import kotlin.uuid.ExperimentalUuidApi

/**
 * [WebGpuRenderer] supporting multiple [VertexKind]s.
 *
 * @param enableDebugLabels if `true`, buffer, render pipelines, shaders, and other GPU objects will be labeled,
 * potentially using more GPU resources than strictly necessary, increasing VRAM usage.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalSpockApi::class, ExperimentalUuidApi::class)
public class AdvancedRenderer(
	override val device: GPUDevice,
	override val surface: Surface,
	transpiler: Shader.Transpiler.Simple<Wgsl, StringShader.FormFactor>,
	override val format: GPUTextureFormat = surface.textureFormat,
	public val backgroundColor: GPUColor = Color(.0, .0, .0, 1.0),
	enableDebugLabels: Boolean = true,
	private val byteArrayPool: InferringCache<Int, ByteArray> = NoopCache(
		producer = ::ByteArray,
		inferrer = ByteArray::size
	),
	logLevel: Level = Level.DEBUG,
	private val renderLogLevel: Level = Level.OFF,
	public val scene: RenderScene
) : WebGpuRenderer, Closer by Closer(surface, device, logLevel = logLevel) {
	private val isConfigured = Flag()
	private val shaders = +ShaderCache(transpiler)
	private val shaderDescriptors = +EverlastingCache<Shader, ShaderModuleDescriptor> {
		ShaderModuleDescriptor(
			code = (it as StringShader).code,
			label = if (enableDebugLabels) it.label else emptyString()
		)
	}
	private val shaderModules = +EverlastingCache(producer = device::createShaderModule)
	private val meshBuffers = +PruningCache<VertexKind<*>, MeshStorage<*>> { MeshStorage(it, byteArrayPool) }

	private val vertexLayouts = +EverlastingCache<VertexKind<*>, GPUVertexBufferLayout> {
		var size = 0uL

		VertexBufferLayout(
			attributes = buildList {
				for ((index, attribute) in it.attributes.withIndex()) {
					this += VertexAttribute(
						format = attribute,
						offset = size,
						shaderLocation = index.toUInt()
					)

					size += attribute.sizeInBytes().toULong()
				}
			}.toImmutableList(),
			arrayStride = size
		)
	}

	private suspend fun Shader.transpile(): Shader = shaders[this] ?: throw UnsupportedShaderException(this)

	private val renderPipelines = +EverlastingCache<Pipeline<*>, GPURenderPipelineDescriptor> {
		RenderPipelineDescriptor(
			label = if (enableDebugLabels) it.label else "",
			vertex = VertexState(
				module = shaderModules[shaderDescriptors[it.vertexShader.transpile()]],
				entryPoint = (it.vertexShader as Wgsl.Shader).entrypoint,
				constants = it.vertexShader.constants,
				buffers = listOf(vertexLayouts[it.vertexShader.vertexKind])
			),
			fragment = it.fragmentShader?.let { shader ->
				FragmentState(
					module = shaderModules[shaderDescriptors[shader.transpile()]],
					entryPoint = (shader as Wgsl.Shader).entrypoint,
					// TODO: support converting texture formats
					targets = listOf(ColorTargetState(shader.textureFormat))
				)
			},
			depthStencil = DepthStencilState(
				depthWriteEnabled = true,
				depthCompare = GPUCompareFunction.Less,
				format = GPUTextureFormat.Depth24Plus
			),
			primitive = PrimitiveState(
				topology = GPUPrimitiveTopology.TriangleList,
				stripIndexFormat = null,
				frontFace = GPUFrontFace.CCW,
				cullMode = GPUCullMode.None
			)
		)
	} then +PruningCache(producer = device::createRenderPipeline)

	private val vertexBufferPool = +PruningPool(
		producer = {
			device.createBuffer(
				descriptor = BufferDescriptor(
					size = it,
					usage = setOf(GPUBufferUsage.Vertex, GPUBufferUsage.CopyDst)
				)
			)
		},
		inferrer = GPUBuffer::size
	)

	private val vertexBuffers =
		+BorrowingCache<Pair<VertexKind<*>, GPUSize64>, GPUSize64, GPUBuffer>(from = vertexBufferPool) { (_, size) -> size }

	private val indexBufferPool = +PruningPool(
		producer = {
			device.createBuffer(
				descriptor = BufferDescriptor(
					size = it,
					usage = setOf(GPUBufferUsage.Index, GPUBufferUsage.CopyDst)
				)
			)
		},
		inferrer = GPUBuffer::size
	)

	private val indexBuffers = +BorrowingCache<Mesh<*>, GPUSize64, GPUBuffer>(from = indexBufferPool) {
		it.indices.size.toULong() * UInt.SIZE_BYTES.toULong()
	}

	private val depthTexture = suspendLazy {
		+device.createTexture(
			TextureDescriptor(
				size = (surface.window.size.await() z 1u).asSize3D(),
				format = GPUTextureFormat.Depth24Plus,
				usage = setOf(GPUTextureUsage.RenderAttachment)
			)
		)
	}

	override suspend fun invoke(): Unit = autoclose(renderLogLevel) {
		isConfigured.set {
			surface.configure(SurfaceConfiguration(device, format))
		}

		val batches = scene()

		withContext(Dispatchers.Default) {
			launch { meshBuffers.tick() }
			launch { renderPipelines.tick() }
			launch {
				shaders.cacheAll(
					emptySequence<Shader>()
							+ batches.asSequence().map { it.pipeline.vertexShader }
							+ batches.asSequence().mapNotNull { it.pipeline.fragmentShader }
				)
			}
		}

		val encoder = +device.createCommandEncoder()

		for ((vertexKind, batches) in batches.groupBy { it.pipeline.vertexShader.vertexKind }) {
			val meshes = batches.map { it.mesh }
			val storage = meshBuffers[vertexKind]

			@Suppress("UNCHECKED_CAST")
			val data = (storage as MeshStorage<Any>)[meshes]
			val vertexBuffer = vertexBuffers[vertexKind, data.size.toULong()]

			@Suppress("DEPRECATION") // TODO: fix this
			device.queue.writeBuffer(vertexBuffer, bufferOffset = 0uL, data)

			val renderPass = encoder.beginRenderPass(
				RenderPassDescriptor(
					listOf(
						RenderPassColorAttachment(
							view = surface.currentTexture.texture.createView(),
							loadOp = GPULoadOp.Clear,
							clearValue = backgroundColor,
							storeOp = GPUStoreOp.Store
						)
					),
					depthStencilAttachment = RenderPassDepthStencilAttachment(
						view = depthTexture().createView(),
						depthClearValue = 1.0f,
						depthLoadOp = GPULoadOp.Clear,
						depthStoreOp = GPUStoreOp.Store
					)
				)
			)

			renderPass.setVertexBuffer(0u, vertexBuffer)

			for ((pipeline, mesh) in batches) {
				val indexBuffer = indexBuffers[mesh]

				@Suppress("DEPRECATION") // TODO: fix this
				device.queue.writeBuffer(indexBuffer, bufferOffset = 0uL, storage.reconstructIndices(mesh))

				renderPass.setPipeline(renderPipelines[pipeline])
				renderPass.setIndexBuffer(indexBuffer, GPUIndexFormat.Uint32)
				renderPass.drawIndexed(mesh.indices.size.toUInt())
			}

			renderPass.end()
		}

		device.queue.submit(listOf(encoder.finish()))
	}
}
