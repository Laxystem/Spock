package quest.laxla.spock.example

import io.ygdrasil.webgpu.*
import quest.laxla.spock.*
import quest.laxla.spock.toolkit.Surface
import quest.laxla.spock.toolkit.WebGpuRenderer

internal val Surface.textureFormat
	get() = preferredTextureFormat
		?: GPUTextureFormat.RGBA8Unorm.takeIf { it in supportedTextureFormats }
		?: GPUTextureFormat.BGRA8Unorm.takeIf { it in supportedTextureFormats }
		?: supportedTextureFormats.first()

@OptIn(ExperimentalSpockApi::class)
public class MyRenderer(
	override val device: GPUDevice,
	override val surface: Surface,
	override val format: GPUTextureFormat = surface.textureFormat
) : WebGpuRenderer, Closer by Closer() {
	private val vertices = floatArrayOf(
		-0.8f, -0.8f,
		+0.8f, -0.8f,
		+0.8f, +0.8f,

		-0.8f, -0.8f,
		+0.8f, +0.8f,
		-0.8f, +0.8f
	)

	private val isConfigured = Flag()

	private val vertexBufferLayout = VertexBufferLayout(
		arrayStride = Float.SIZE_BYTES.toULong() * 2uL,
		attributes = listOf(
			VertexAttribute(
				format = GPUVertexFormat.Float32x2,
				offset = 0u,
				shaderLocation = 0u
			)
		)
	)

	private val shaders = +device.createShaderModule(
		ShaderModuleDescriptor(
			label = "shader",
			// language=wgsl
			code = """
				@vertex
				fn vertexMain(@location(0) position: vec2f) -> @builtin(position) vec4f {
					return vec4f(position.x, position.y, 0, 1);
				}
				
				@fragment
				fn fragmentMain() -> @location(0) vec4f {
					return vec4f(0, 0, 0, 1);
				}
			""".trimIndent()
		)
	)

	private val renderPipeline = +device.createRenderPipeline(
		RenderPipelineDescriptor(
			label = "pipeline",
			vertex = VertexState(
				module = shaders,
				entryPoint = "vertexMain",
				buffers = listOf(vertexBufferLayout)
			),
			fragment = FragmentState(
				module = shaders,
				entryPoint = "fragmentMain",
				targets = listOf(ColorTargetState(format))
			),
			primitive = PrimitiveState(
				topology = GPUPrimitiveTopology.TriangleList,
				stripIndexFormat = null,
				frontFace = GPUFrontFace.CCW,
				cullMode = GPUCullMode.None
			)
		)
	)

	private val vertexBuffer = +device.createBuffer(
		BufferDescriptor(
			label = "Vertices",
			size = vertices.size.toULong() * Float.SIZE_BYTES.toULong(),
			usage = setOf(GPUBufferUsage.Vertex, GPUBufferUsage.CopyDst)
		)
	)

	override suspend fun invoke(): Unit = autoclose {
		isConfigured.set {
			surface.configure(SurfaceConfiguration(device, format))
		}

		val encoder = +device.createCommandEncoder()

		@Suppress("DEPRECATION") // TODO: fix this
		device.queue.writeBuffer(vertexBuffer, bufferOffset = 0u, vertices)

		encoder.beginRenderPass(
			RenderPassDescriptor(
				colorAttachments = listOf(
					RenderPassColorAttachment(
						view = +surface.currentTexture.texture.createView(),
						loadOp = GPULoadOp.Clear,
						clearValue = Color(0.2, 0.40, 0.84, a = 1.0),
						storeOp = GPUStoreOp.Store
					)
				)
			)
		).apply {
			setPipeline(renderPipeline)
			setVertexBuffer(0u, vertexBuffer)
			draw(vertices.size.toUInt() / 2u)

			end()
		}

		device.queue.submit(listOf(encoder.finish()))
	}
}
