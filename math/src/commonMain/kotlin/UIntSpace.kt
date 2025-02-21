@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.IntSpace.append

/**
 * @since 0.0.1-alpha.4
 * @see UInt
 */
@OptIn(ExperimentalSpockApi::class)
@Typealiased("#ui", UInt::class)
public data object UIntSpace : NumeralSpace<UInt>, BufferableSpace<UInt> {
	override fun UInt.minus(other: UInt): UInt = this - other

	override fun UInt.plus(other: UInt): UInt = this + other

	override fun UInt.times(other: UInt): UInt = this * other

	override fun UInt.div(other: UInt): UInt = this / other

	override fun UInt.rem(other: UInt): UInt = this % other
	
	override val unit: UInt get() = 1u
	
	override val zero: UInt get() = 0u
	
	override val sizeInBytes: UInt get() = UInt.SIZE_BYTES.toUInt()
	
	override val sizeInBits: UInt get() = UInt.SIZE_BITS.toUInt()

	override fun ByteAppender.append(uInt: UInt) = append(uInt.toInt())
}
