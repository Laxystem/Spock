package quest.laxla.spock.math

import kotlinx.io.bytestring.ByteStringBuilder
import quest.laxla.spock.ExperimentalSpockApi

/**
 * Calculates byte size based on [bit count][quest.laxla.spock.math.SizedSpace.sizeInBits]
 * and [byte count][quest.laxla.spock.math.SizedSpace.sizeInBytes].
 *
 * This function is experimental, as it may be added into the interface itself.
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi
public val SizedSpace<*>.byteSize: UInt get() = sizeInBits / sizeInBytes

/**
 * Appends the contents of this [Vector1] into this [ByteStringBuilder].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteStringBuilder.append(vector: Vector1<V, S>): Unit where S : BufferableSpace<V> =
	with(vector.space) {
		append(vector.x)
	}

/**
 * Appends the contents of this [Vector2] into this [ByteStringBuilder].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteStringBuilder.append(vector: Vector2<V, S>): Unit where S : BufferableSpace<V> =
	with(vector.space) {
		append(vector.x)
		append(vector.y)
	}

/**
 * Appends the contents of this [Vector3] into this [ByteStringBuilder].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteStringBuilder.append(vector: Vector3<V, S>): Unit where S : BufferableSpace<V> =
	with(vector.space) {
		append(vector.x)
		append(vector.y)
		append(vector.z)
	}

/**
 * Appends the contents of this [Vector4] into this [ByteStringBuilder].
 *
 * @since 0.0.1-alpha.4
 * @see BufferableSpace.append
 */
public fun <V, S> ByteStringBuilder.append(vector: Vector4<V, S>): Unit where S : BufferableSpace<V> =
	with(vector.space) {
		append(vector.x)
		append(vector.y)
		append(vector.z)
		append(vector.w)
	}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
internal fun ByteStringBuilder.append(int: Int, sizeInBytes: UInt) {
	for (i in 0u..<sizeInBytes step Byte.SIZE_BITS) append((int shr i.toInt()).toByte())
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
internal fun ByteStringBuilder.append(long: Long, sizeInBytes: UInt) {
	for (i in 0u..<sizeInBytes step Byte.SIZE_BITS) append((long shr i.toInt()).toByte())
}
