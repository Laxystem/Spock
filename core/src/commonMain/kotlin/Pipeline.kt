package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

/**
 * Describes how meshes should be rendered.
 *
 * @since 0.0.1-alpha.4
 * @see Batch
 */
@ExperimentalSpockApi
public data class Pipeline<Vertex : Any>(
	public val vertexShader: VertexShader<Vertex>,
	public val fragmentShader: FragmentShader?,
	public val label: String? = null
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
	) {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public constructor(
			vertexShader: VertexShader<Vertex>,
			fragmentShader: FragmentShader?,
			mesh: Mesh<Vertex>,
			pipelineLabel: String? = null
		) : this(Pipeline(vertexShader, fragmentShader, pipelineLabel), mesh)

		/**
		 * @since 0.0.1-alpha.4
		 */
		public constructor(
			vertexShader: VertexShader<Vertex>,
			fragmentShader: FragmentShader?,
			vertices: ImmutableList<Vertex>,
			indices: ImmutableList<UInt>,
			pipelineLabel: String? = null
		) : this(vertexShader, fragmentShader, Mesh(vertices, indices), pipelineLabel)

		/**
		 * @since 0.0.1-alpha.4
		 */
		public constructor(
			vertexShader: VertexShader<Vertex>,
			fragmentShader: FragmentShader?,
			vararg vertices: Vertex,
			pipelineLabel: String? = null
		) : this(vertexShader, fragmentShader, Mesh(*vertices), pipelineLabel)

		/**
		 * @since 0.0.1-alpha.4
		 */
		@ExperimentalUnsignedTypes
		public constructor(
			vertexShader: VertexShader<Vertex>,
			fragmentShader: FragmentShader?,
			vertices: ImmutableList<Vertex>,
			vararg indices: UInt,
			pipelineLabel: String? = null
		) : this(vertexShader, fragmentShader, vertices, indices.toPersistentList(), pipelineLabel)

		/**
		 * @since 0.0.1-alpha.4
		 */
		public constructor(pipeline: Pipeline<Vertex>, vararg vertices: Vertex) : this(pipeline, Mesh(*vertices))

		/**
		 * @since 0.0.1-alpha.4
		 */
		@ExperimentalUnsignedTypes
		public constructor(pipeline: Pipeline<Vertex>, vertices: ImmutableList<Vertex>, vararg indices: UInt) :
				this(pipeline, Mesh(vertices, indices.toPersistentList()))
	}
}
