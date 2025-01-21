package quest.laxla.spock

import kotlinx.io.bytestring.ByteStringBuilder

/**
 * Describes how [Vertex] serializes as a vertex into buffers to be sent to the GPU.
 *
 * @since 0.0.1-alpha.4
 */
public interface VertexKind<Vertex : Any> {
	/**
	 * The total size of a vertex of this kind.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val sizeInBytes: UInt

	/**
	 * Appends this [vertex] into this [ByteStringBuilder].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun ByteStringBuilder.append(vertex: Vertex)

	/**
	 * An object that requires a [VertexKind].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public interface Bound<Vertex : Any> {
		/**
		 * @since 0.0.1-alpha.4
		 * @see VertexKind
		 * @see VertexKind.Bound
		 */
		public val vertexKind: VertexKind<Vertex>
	}
}
