package quest.laxla.spock.example

import io.ygdrasil.webgpu.*
import io.ygdrasil.webgpu.RenderPipelineDescriptor.VertexState.VertexBufferLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quest.laxla.spock.*
import quest.laxla.spock.toolkit.Surface
import quest.laxla.spock.toolkit.WebGpuRenderer
import kotlin.uuid.ExperimentalUuidApi

@Suppress("UNCHECKED_CAST")
private suspend operator fun <T : Any> Cache<VertexKind<T>, MeshStorage<*>>.get(
	vertexKind: VertexKind<T>,
	meshes: Collection<Mesh<*>>
): ByteArray = (this[vertexKind] as MeshStorage<Any>)[meshes]

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
	override val format: TextureFormat = surface.textureFormat,
	public val scene: RenderScene,
	transpiler: Shader.Transpiler.Simple<Wgsl, StringShader.FormFactor>,
	enableDebugLabels: Boolean = true,
	private val byteArrayPool: InferringCache<Int, ByteArray> = NoopCache(
		producer = ::ByteArray,
		inferrer = ByteArray::size
	)
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
				constants = (it.vertexShader as Wgsl.Shader).constants,
				buffers = listOf(vertexLayouts[it.vertexShader.vertexKind])
			)
		)
	} then +PruningCache(producer = device::createRenderPipeline)

	private val buffers = +PruningPool(
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

	override suspend fun invoke(): Unit = autoclose {
		isConfigured.set {
			surface.configure(SurfaceConfiguration(device, format))
		}

		val batches = scene()

		withContext(Dispatchers.Default) {
			launch { meshBuffers.tick() }
			launch { renderPipelines.tick() }
			launch { shaders.cacheAll(batches.asSequence().map { it.pipeline.vertexShader }) }
		}

		val encoder = +device.createCommandEncoder()

		for ((vertexKind, meshes) in batches.groupBy(
			keySelector = { it.pipeline.vertexShader.vertexKind },
			valueTransform = { it.mesh }
		)) {
			val data = meshBuffers[vertexKind, meshes]

			val buffer = device.queue.writeBuffer(buffers[data.size.toULong()], bufferOffset = 0uL, data)

			TODO("render passes")
		}
	}
}
