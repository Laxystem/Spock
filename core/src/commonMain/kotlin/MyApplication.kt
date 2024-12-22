package quest.laxla.spock

import io.ygdrasil.wgpu.*

private val Surface.canvasFormat
	get() = preferredCanvasFormat
		?: TextureFormat.rgba8unorm.takeIf { it in supportedFormats }
		?: TextureFormat.bgra8unorm.takeIf { it in supportedFormats }
		?: supportedFormats.first()

public class MyApplication(
	override val device: Device,
	override val surface: Surface,
	override val format: TextureFormat = surface.canvasFormat,
) : WebGpuApplication, Closer by Closer(surface.asSuspendCloseable(), device.asSuspendCloseable()) {
	private val vertices = floatArrayOf(
		-0.8f, -0.8f,
		+0.8f, -0.8f,
		+0.8f, +0.8f,

		-0.8f, -0.8f,
		+0.8f, +0.8f,
		-0.8f, +0.8f,
	)

	init {
		surface.configure(CanvasConfiguration(device, format))
	}

	private val vertexBufferLayout = RenderPipelineDescriptor.VertexState.VertexBufferLayout(
		arrayStride = Float.SIZE_BYTES * 2L,
		attributes = listOf(
			RenderPipelineDescriptor.VertexState.VertexBufferLayout.VertexAttribute(
				format = VertexFormat.float32x2,
				offset = 0,
				shaderLocation = 0
			)
		)
	)

	private val shaders = +device.createShaderModule(
		ShaderModuleDescriptor(
			label = "shader",
			//language=wgsl
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
			vertex = RenderPipelineDescriptor.VertexState(
				module = shaders,
				entryPoint = "vertexMain",
				buffers = listOf(vertexBufferLayout)
			),
			fragment = RenderPipelineDescriptor.FragmentState(
				module = shaders,
				entryPoint = "fragmentMain",
				targets = listOf(RenderPipelineDescriptor.FragmentState.ColorTargetState(format))
			)
		)
	)
	
	private val vertexBuffer = +device.createBuffer(
		BufferDescriptor(
			label = "Vertices",
			size = vertices.size * Float.SIZE_BYTES.toLong(),
			usage = setOf(BufferUsage.vertex, BufferUsage.copydst)
		)
	)
	
	override suspend fun renderFrame(): Unit = autoclose {
		val encoder = +device.createCommandEncoder()
		
		device.queue.writeBuffer(vertexBuffer, bufferOffset = 0, vertices)
		
		encoder.beginRenderPass(RenderPassDescriptor(
			colorAttachments = listOf(
				RenderPassDescriptor.ColorAttachment(
					view = +surface.getCurrentTexture().createView(),
					loadOp = LoadOp.clear,
					clearValue = Color(0.2, 0.40, 0.84, alpha = 1.0),
					storeOp = StoreOp.store
				)
			)
		)).apply {
			setPipeline(renderPipeline)
			setVertexBuffer(0, vertexBuffer)
			draw(vertices.size / 2)

			end()
		}

		device.queue.submit(listOf(encoder.finish()))
	}
}
