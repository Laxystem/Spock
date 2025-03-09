package quest.laxla.spock

import io.ygdrasil.webgpu.VertexFormat
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import quest.laxla.spock.math.Vector1f
import quest.laxla.spock.math.Vector2f
import quest.laxla.spock.math.Vector3f
import quest.laxla.spock.math.append

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

/**
 * @since 0.0.1-alpha.4
 */
public data object Vector1fVertexKind :
	VertexKind<Vector1f> by VertexKind(VertexFormat.Float32, append = ByteAppender::append)

/**
 * @since 0.0.1-alpha.4
 */
public data object Vector2fVertexKind :
	VertexKind<Vector2f> by VertexKind(VertexFormat.Float32x2, append = ByteAppender::append)

/**
 * @since 0.0.1-alpha.4
 */
public data object Vector3fVertexKind :
	VertexKind<Vector3f> by VertexKind(VertexFormat.Float32x3, append = ByteAppender::append)
