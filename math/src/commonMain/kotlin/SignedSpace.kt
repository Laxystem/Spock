package quest.laxla.spock.math

import quest.laxla.spock.math.Vectorization.Kind.Disabled
import quest.laxla.spock.math.Vectorization.Kind.Exact

public interface SignedSpace<@Vectorization(kind = Exact) V> : NumeralSpace<V> where V : Comparable<V> {
	/**
	 * Given `x`, returns `y` where `0 - x = y`.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@VectorOperation
	public operator fun V.unaryMinus(): V = zero - this

	/**
	 * @since 0.0.1-alpha.4
	 * @see Int.unaryPlus
	 */
	@VectorOperation
	public operator fun V.unaryPlus(): V = this


	/**
	 * Calculates the remainder of flooring division of [this] value (dividend) by the [divisor].
	 *
	 * The result is either zero or has the same sign as the divisor,
	 * and its absolute value is smaller than the divisor's.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@VectorOperation
	public infix fun V.mod(@Vectorization(kind = Disabled) divisor: V): V

	/**
	 * @since 0.0.1-alpha.4
	 */
	@VectorOperation
	public fun V.abs(): V
}
