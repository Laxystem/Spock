@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import kotlin.math.absoluteValue

/**
 * @since 0.0.1-alpha.4
 * @see Int
 */
@Typealiased("#i", Int::class)
public data object IntSpace : SignedSpace<Int>, BufferableSpace<Int> {
	override fun Int.mod(divisor: Int): Int = mod(other = divisor)

	override fun Int.abs(): Int = absoluteValue

	override fun Int.minus(other: Int): Int = this - other

	override fun Int.plus(other: Int): Int = this + other

	override fun Int.times(other: Int): Int = this * other

	override fun Int.div(other: Int): Int = this / other

	override fun Int.rem(other: Int): Int = this % other

	override val unit: Int get() = 1

	override val zero: Int get() = 0

	override val sizeInBytes: UInt get() = Int.SIZE_BYTES.toUInt()

	override val sizeInBits: UInt get() = Int.SIZE_BITS.toUInt()

	override fun ByteAppender.append(value: Int) = append(value, sizeInBytes)
}
