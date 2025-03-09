package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableMap

/**
 * Shader of this [vertexKind].
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi // add mesh shader support
public interface VertexShader<Vertex : Any> : VertexKind.Bound<Vertex>, Shader {
	/**
	 * Constants passed to the GPU available in the shader.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val constants: ImmutableMap<String, Double>
}
