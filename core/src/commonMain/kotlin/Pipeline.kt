package quest.laxla.spock

/**
 * Describes how [mesh] should be rendered.
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi
public data class Pipeline<Vertex : Any>(
	public val mesh: Mesh<Vertex>,
	public val vertexShader: VertexShader<Vertex>
)
