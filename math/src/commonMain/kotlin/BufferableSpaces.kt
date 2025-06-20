package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import quest.laxla.spock.appendToByteString

/**
 * Appends the contents of this [Vector1] into this [appendToByteString].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteAppender.append(vector: Vector1<V, S>): Unit where S : BufferableSpace<V> = with(vector.space) {
	append(vector.x)
}

/**
 * Appends the contents of this [Vector2] into this [appendToByteString].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteAppender.append(vector: Vector2<V, S>): Unit where S : BufferableSpace<V> = with(vector.space) {
	append(vector.x)
	append(vector.y)
}

/**
 * Appends the contents of this [Vector3] into this [appendToByteString].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteAppender.append(vector: Vector3<V, S>): Unit where S : BufferableSpace<V> = with(vector.space) {
	append(vector.x)
	append(vector.y)
	append(vector.z)
}

/**
 * Appends the contents of this [Vector4] into this [appendToByteString].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteAppender.append(vector: Vector4<V, S>): Unit where S : BufferableSpace<V> = with(vector.space) {
	append(vector.x)
	append(vector.y)
	append(vector.z)
	append(vector.w)
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
internal fun ByteAppender.append(int: Int, sizeInBits: UInt) {
	for (i in 0u..<sizeInBits step Byte.SIZE_BITS) append((int shr i.toInt()).toByte())
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
internal fun ByteAppender.append(long: Long, sizeInBits: UInt) {
	for (i in 0u..<sizeInBits step Byte.SIZE_BITS) append((long shr i.toInt()).toByte())
}
