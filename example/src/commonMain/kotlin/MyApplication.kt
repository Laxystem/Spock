package quest.laxla.spock.example

import io.ygdrasil.webgpu.Color
import io.ygdrasil.webgpu.GPUVertexFormat
import kotlinx.collections.immutable.persistentListOf
import quest.laxla.spock.*
import quest.laxla.spock.math.*
import quest.laxla.spock.toolkit.webGpuApplication

// do not use in shader; IntelliJ formatting shenanigans
private const val vertexMain = "vertexMain"
private const val fragmentMain = "fragmentMain"

// language=wgsl
private const val shader = """
	struct VertexOutput {
		@builtin(position) position: vec4f,
		@location(0) color: vec3f
	}
	
	@vertex
	fn vertexMain(@location(0) position: vec2f, @location(1) color: vec3f) -> VertexOutput {
		var output: VertexOutput;
		output.position = vec4f(position, 0, 1);
		output.color = color;
		return output;
	}
	
	@fragment
	fn fragmentMain(output: VertexOutput) -> @location(0) vec4f {
		return vec4(output.color, 1);
	}
"""

private fun ByteAppender.append(vertex: Vertex2fRgba) {
	append(vertex.position)
	append(vertex.color)
}

private data class Vertex2fRgba(val position: Vector2f, val color: Vector3f) {
	constructor(x: Float, y: Float, color: Vector3f) : this(FloatSpace.vectorOf(x, y), color)

	companion object : VertexKind<Vertex2fRgba> by VertexKind(
		GPUVertexFormat.Float32x2,
		GPUVertexFormat.Float32x3,
		append = ByteAppender::append
	)
}

@OptIn(ExperimentalUnsignedTypes::class)
public suspend fun myApplication(title: String): Unit = webGpuApplication(title) { device, surface ->
	val kind = Shader.Kind(Wgsl, StringShader)
	val background = FloatSpace.vectorOf(x = .2f, y = .4f, z = .84f)

	val batches = persistentListOf(
		Pipeline.Batch(
			vertexShader = wgslVertexShaderOf(
				wgsl = shader,
				vertexKind = Vertex2fRgba,
				entrypoint = vertexMain
			),
			fragmentShader = wgslFragmentShaderOf(
				wgsl = shader,
				textureFormat = surface.textureFormat,
				entrypoint = fragmentMain
			),
			vertices = persistentListOf(
				Vertex2fRgba(-.8f, -.8f, FloatSpace.vectorOfZeros().asVector3()),
				Vertex2fRgba(-.8f, +.8f, FloatSpace.vectorOfOnes().asVector3()),
				Vertex2fRgba(+.8f, +.8f, FloatSpace.vectorOfZeros().asVector3()),
				Vertex2fRgba(+.8f, -.8f, background),
			),
			indices = persistentListOf(
				0u, 1u, 2u,
				0u, 2u, 3u,
			)
		)
	)

	AdvancedRenderer(
		device,
		surface,
		NoopShaderTranspiler(kind),
		backgroundColor = Color(
			background.x.toDouble(),
			background.y.toDouble(),
			background.z.toDouble(),
			1.0
		)
	) { batches }
}
