package quest.laxla.spock.example

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
		return vec4f(0, 0, 0, 1);
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
				FloatSpace.vectorOf(0f, 0f),
				FloatSpace.vectorOf(0f, 1f),
				FloatSpace.vectorOf(1f, 0f),
				FloatSpace.vectorOf(1f, 1f)
			),
			indices = persistentListOf(0u, 1u, 2u, 1u, 2u, 3u)
		)
	)

	AdvancedRenderer(
		device,
		surface,
		NoopShaderTranspiler(kind)
	) { batches }
}
