package quest.laxla.spock

/**
 * Shader of this [vertexKind].
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi // add mesh shader support
public interface VertexShader<Vertex : Any> : VertexKind.Bound<Vertex>, Shader
