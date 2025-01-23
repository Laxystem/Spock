package quest.laxla.spock

import io.ygdrasil.webgpu.VertexFormat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.io.bytestring.ByteStringBuilder

/**
 * Serializes [Vertex] into buffers to be sent to the GPU.
 *
 * @since 0.0.1-alpha.4
 */
public interface VertexKind<Vertex : Any> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val attributes: @FutureImmutableArray ImmutableList<VertexFormat>

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
