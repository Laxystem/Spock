@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import kotlinx.io.bytestring.ByteStringBuilder
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.IntSpace.append
import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * @since 0.0.1-alpha.4
 * @see Float
 */
@OptIn(ExperimentalSpockApi::class)
@Typealiased("#f", Float::class)
public object FloatSpace : FractionalSpace<Float>, SignedSpace<Float>, BufferableSpace<Float> {
	override fun Float.pow(exponent: Float): Float = pow(x = exponent)

	override fun Float.minus(other: Float): Float = this - other

	override fun Float.plus(other: Float): Float = this + other

	override fun Float.times(other: Float): Float = this * other

	override fun Float.div(other: Float): Float = this / other

	override fun Float.rem(other: Float): Float = this % other

	override val unit: Float get() = 1f
	
	override val zero: Float get() = 0f

	override fun Float.mod(divisor: Float): Float = mod(other = divisor)

	override fun Float.abs(): Float = absoluteValue

	override val sizeInBytes: UInt get() = Float.SIZE_BYTES.toUInt()
	override val sizeInBits: UInt get() = Float.SIZE_BITS.toUInt()

	override fun Float.sqrt(): Float = kotlin.math.sqrt(this)

	override fun ByteStringBuilder.append(float: Float) = append(float.toBits())
}
