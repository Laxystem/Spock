@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import kotlin.math.absoluteValue

/**
 * @since 0.0.1-alpha.4
 * @see Byte
 */
@Typealiased("#b", Byte::class)
public data object ByteSpace : SignedSpace<Byte>, BufferableSpace<Byte> {
	override fun Byte.mod(divisor: Byte): Byte = mod(other = divisor)

	override fun Byte.abs(): Byte = toInt().absoluteValue.toByte()

	override fun Byte.minus(other: Byte): Byte = (this - other).toByte()

	override fun Byte.plus(other: Byte): Byte = (this + other).toByte()

	override fun Byte.times(other: Byte): Byte = (this * other).toByte()

	override fun Byte.div(other: Byte): Byte = (this / other).toByte()

	override fun Byte.rem(other: Byte): Byte = (this % other).toByte()

	override val unit: Byte get() = 1

	override val zero: Byte get() = 0

	override val sizeInBytes: UInt get() = Byte.SIZE_BYTES.toUInt()

	override val sizeInBits: UInt get() = Byte.SIZE_BITS.toUInt()

	override fun ByteAppender.append(value: Byte) = append(value)
}
