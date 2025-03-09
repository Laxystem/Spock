@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import quest.laxla.spock.ExperimentalSpockApi
import kotlin.math.absoluteValue

/** 
 * @since 0.0.1-alpha.4
 * @see Long
 */
@OptIn(ExperimentalSpockApi::class)
@Typealiased("#l", Long::class)
public data object LongSpace : SignedSpace<Long>, BufferableSpace<Long> {
	override fun Long.mod(divisor: Long): Long = mod(other = divisor)

	override fun Long.abs(): Long = absoluteValue

	override fun Long.minus(other: Long): Long = this - other

	override fun Long.plus(other: Long): Long = this + other

	override fun Long.times(other: Long): Long = this * other

	override fun Long.div(other: Long): Long = this / other

	override fun Long.rem(other: Long): Long = this % other
	
	override val unit: Long get() = 1
	
	override val zero: Long get() = 0
	
	override val sizeInBytes: UInt get() = Long.SIZE_BYTES.toUInt()
	
	override val sizeInBits: UInt get() = Long.SIZE_BITS.toUInt()

	override fun ByteAppender.append(value: Long) = append(value, sizeInBits)
}
