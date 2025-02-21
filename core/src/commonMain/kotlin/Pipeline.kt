package quest.laxla.spock

/**
 * Describes how meshes should be rendered.
 *
 * @since 0.0.1-alpha.4
 * @see Batch
 */
@ExperimentalSpockApi
public data class Pipeline<Vertex : Any>(
	public val vertexShader: VertexShader<Vertex>,
	public val label: String
) {
	/**
	 * Represents a 'batch' of data to be sent to the GPU.
	 *
	 * Note this is an abstraction;
	 * [Renderer]s may manipulate this data before sending it to the GPU to improve performance.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public data class Batch<Vertex : Any>(
		val pipeline: Pipeline<Vertex>,
		val mesh: Mesh<Vertex>
	)
}
