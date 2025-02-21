package quest.laxla.spock

import io.ygdrasil.webgpu.VertexFormat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Creates a [VertexKind] for the provided [attributes].
 *
 * @since 0.0.1-alpha.4
 */
public inline fun <V : Any> VertexKind(
	vararg attributes: VertexFormat,
	crossinline append: ByteAppender.(V) -> Unit
): VertexKind<V> = object : VertexKind<V> {
	override val attributes: @FutureImmutableArray ImmutableList<VertexFormat> = persistentListOf(*attributes)
	override fun ByteAppender.append(vertex: V) = append(vertex)

	override fun toString(): String = "VertexKind(${this.attributes})"
	override fun equals(other: Any?): Boolean = other is VertexKind<*> && this.attributes == other.attributes
	override fun hashCode(): Int = attributes.hashCode()
}
