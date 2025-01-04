@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import kotlin.mod as kmod

public object IntSpace : SignedSpace<Int>, SizedSpace<Int> {
	override fun Int.mod(divisor: Int): Int = kmod(divisor)

	override fun Int.abs(): Int = kotlin.math.abs(this)

	override fun Int.minus(other: Int): Int = this - other

	override fun Int.plus(other: Int): Int = this + other

	override fun Int.times(other: Int): Int = this * other

	override fun Int.div(other: Int): Int = this / other

	override fun Int.rem(other: Int): Int = this % other
	
	override val unit: Int
		get() = 1
	
	override val zero: Int
		get() = 0
	
	override val sizeInBytes: Int
		get() = Int.SIZE_BYTES
	
	override val sizeInBits: Int
		get() = Int.SIZE_BITS
}
