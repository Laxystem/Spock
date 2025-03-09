package quest.laxla.spock.example

import io.ygdrasil.webgpu.*
import io.ygdrasil.webgpu.RenderPipelineDescriptor.VertexState.VertexBufferLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quest.laxla.spock.*
import quest.laxla.spock.math.z
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
	override val device: Device,
	override val surface: Surface,
	transpiler: Shader.Transpiler.Simple<Wgsl, StringShader.FormFactor>,
	override val format: TextureFormat = surface.textureFormat,
	public val backgroundColor: Color = Color(.0, .0, .0, 1.0),
	enableDebugLabels: Boolean = true,
	private val byteArrayPool: InferringCache<Int, ByteArray> = NoopCache(
		producer = ::ByteArray,
		inferrer = ByteArray::size
	),
	public val scene: RenderScene
) : WebGpuRenderer, Closer by Closer(surface, device) {
	private val isConfigured = Flag()
	private val shaders = +ShaderCache(transpiler)
	private val shaderDescriptors = +EverlastingCache<Shader, ShaderModuleDescriptor> {
		ShaderModuleDescriptor(
			code = (it as StringShader).code,
			label = it.label.takeIf { enableDebugLabels }
		)
	}
	private val shaderModules = +EverlastingCache(producer = device::createShaderModule)
	private val meshBuffers = +PruningCache<VertexKind<*>, MeshStorage<*>> { MeshStorage(it, byteArrayPool) }

	private val vertexLayouts = +EverlastingCache<VertexKind<*>, VertexBufferLayout> {
		var size = 0uL

		VertexBufferLayout(
			attributes = buildList {
				for ((index, attribute) in it.attributes.withIndex()) {
					this += VertexBufferLayout.VertexAttribute(
						format = attribute,
						offset = size,
						shaderLocation = index.toUInt()
					)

					size += attribute.sizeInBytes().toULong()
				}
			},
			arrayStride = size
		)
	}

	private suspend fun Shader.transpile(): Shader = shaders[this] ?: throw UnsupportedShaderException(this)

	private val renderPipelines = +EverlastingCache<Pipeline<*>, RenderPipelineDescriptor> {
		RenderPipelineDescriptor(
			label = it.label.takeIf { enableDebugLabels },
			vertex = RenderPipelineDescriptor.VertexState(
				module = shaderModules[shaderDescriptors[it.vertexShader.transpile()]],
				entryPoint = (it.vertexShader as Wgsl.Shader).entrypoint,
				constants = it.vertexShader.constants,
				buffers = listOf(vertexLayouts[it.vertexShader.vertexKind])
			),
			fragment = it.fragmentShader?.let { shader ->
				RenderPipelineDescriptor.FragmentState(
					module = shaderModules[shaderDescriptors[shader.transpile()]],
					entryPoint = (shader as Wgsl.Shader).entrypoint,
					// TODO: support converting texture formats
					targets = listOf(RenderPipelineDescriptor.FragmentState.ColorTargetState(shader.textureFormat))
				)
			},
			depthStencil = RenderPipelineDescriptor.DepthStencilState(
				depthWriteEnabled = true,
				depthCompare = CompareFunction.Less,
				format = TextureFormat.Depth24Plus
			),
			primitive = RenderPipelineDescriptor.PrimitiveState(
				topology = PrimitiveTopology.TriangleList,
				stripIndexFormat = null,
				frontFace = FrontFace.CCW,
				cullMode = CullMode.None
			)
		)
	} then +PruningCache(producer = device::createRenderPipeline)

	private val vertexBufferPool = +PruningPool(
		producer = {
			device.createBuffer(
				descriptor = BufferDescriptor(
					size = it,
					usage = setOf(BufferUsage.Vertex, BufferUsage.CopyDst)
				)
			)
		},
		inferrer = Buffer::size
	)

	private val vertexBuffers =
		+BorrowingCache<Pair<VertexKind<*>, GPUSize64>, GPUSize64, Buffer>(from = vertexBufferPool) { (_, size) -> size }

	private val indexBufferPool = +PruningPool(
		producer = {
			device.createBuffer(
				descriptor = BufferDescriptor(
					size = it,
					usage = setOf(BufferUsage.Index, BufferUsage.CopyDst)
				)
			)
		},
		inferrer = Buffer::size
	)

	private val indexBuffers = +BorrowingCache<Mesh<*>, GPUSize64, Buffer>(from = indexBufferPool) {
		it.indices.size.toULong() * UInt.SIZE_BYTES.toULong()
	}

	private val depthTexture = suspendLazy {
		+device.createTexture(
			TextureDescriptor(
				size = (surface.window.size.await() z 1u).asSize3D(),
				format = TextureFormat.Depth24Plus,
				usage = setOf(TextureUsage.RenderAttachment)
			)
		)
	}

	override suspend fun invoke(): Unit = autoclose {
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
			device.queue.writeBuffer(vertexBuffer, bufferOffset = 0uL, data)

			val renderPass = encoder.beginRenderPass(
				RenderPassDescriptor(
					listOf(
						RenderPassDescriptor.ColorAttachment(
							view = surface.currentTexture.texture.createView(),
							loadOp = LoadOp.Clear,
							clearValue = backgroundColor,
							storeOp = StoreOp.Store
						)
					),
					depthStencilAttachment = RenderPassDescriptor.DepthStencilAttachment(
						view = depthTexture().createView(),
						depthClearValue = 1.0f,
						depthLoadOp = LoadOp.Clear,
						depthStoreOp = StoreOp.Store
					)
				)
			)

			renderPass.setVertexBuffer(0u, vertexBuffer)

			for ((pipeline, mesh) in batches) {
				val indexBuffer = indexBuffers[mesh]
				device.queue.writeBuffer(indexBuffer, bufferOffset = 0uL, storage.reconstructIndices(mesh))

				renderPass.setPipeline(renderPipelines[pipeline])
				renderPass.setIndexBuffer(indexBuffer, IndexFormat.Uint32)
				renderPass.drawIndexed(mesh.indices.size.toUInt())
			}

			renderPass.end()
		}

		device.queue.submit(listOf(encoder.finish()))
	}
}
