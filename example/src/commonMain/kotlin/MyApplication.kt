package quest.laxla.spock.example

import io.ygdrasil.webgpu.Color
import kotlinx.collections.immutable.persistentListOf
import quest.laxla.spock.*
import quest.laxla.spock.math.FloatSpace
import quest.laxla.spock.math.vectorOf
import quest.laxla.spock.toolkit.webGpuApplication

// language=wgsl
private const val shader = """
	@vertex
	fn vertexMain(@location(0) position: vec2f) -> @builtin(position) vec4f {
		return vec4f(position.x, position.y, 0, 1);
	}
				
	@fragment
	fn fragmentMain() -> @location(0) vec4f {
		return vec4f(1, 1, 1, 1);
	}
"""

@OptIn(ExperimentalSpockApi::class)
public suspend fun myApplication(title: String): Unit = webGpuApplication(title) { device, surface ->
	val kind = Shader.Kind(Wgsl, StringShader)
	val batches = persistentListOf(
		Pipeline.Batch(
			vertexShader = wgslVertexShaderOf(
				wgsl = shader,
				vertexKind = Vector2fVertexKind,
				entrypoint = "vertexMain"
			),
			fragmentShader = wgslFragmentShaderOf(
				wgsl = shader,
				textureFormat = surface.textureFormat,
				entrypoint = "fragmentMain"
			),
			vertices = persistentListOf(
				FloatSpace.vectorOf(-.8f, -.8f),
				FloatSpace.vectorOf(+.8f, -.8f),
				FloatSpace.vectorOf(+.8f, +.8f),
				FloatSpace.vectorOf(-.8f, +.8f)
			),
			indices = persistentListOf(0u, 1u, 2u, 0u, 2u, 3u)
		)
	)

	AdvancedRenderer(
		device,
		surface,
		NoopShaderTranspiler(kind),
		backgroundColor = Color(0.2, 0.40, 0.84, alpha = 1.0)
	) { batches }
}
