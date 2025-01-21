@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package quest.laxla.spock.math

import kotlinx.io.bytestring.ByteStringBuilder
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.LongSpace.append
import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * @since 0.0.1-alpha.4
 * @see Double
 */
@OptIn(ExperimentalSpockApi::class)
@Typealiased("#d", Double::class)
public object DoubleSpace : FractionalSpace<Double>, SignedSpace<Double>, BufferableSpace<Double> {
	override fun Double.pow(exponent: Double): Double = pow(x = exponent)

	override fun Double.minus(other: Double): Double = this - other

	override fun Double.plus(other: Double): Double = this + other

	override fun Double.times(other: Double): Double = this * other

	override fun Double.div(other: Double): Double = this / other

	override fun Double.rem(other: Double): Double = this % other

	override val unit: Double get() = 1.0
	
	override val zero: Double get() = 0.0

	override fun Double.mod(divisor: Double): Double = mod(other = divisor)

	override fun Double.abs(): Double = absoluteValue

	override val sizeInBytes: UInt get() = Double.SIZE_BYTES.toUInt()
	override val sizeInBits: UInt get() = Double.SIZE_BITS.toUInt()

	override fun Double.sqrt(): Double = kotlin.math.sqrt(this)

	override fun ByteStringBuilder.append(double: Double) = append(double.toBits())
}
