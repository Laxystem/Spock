package quest.laxla.spock.math

import quest.laxla.spock.math.Vectorization.Kind.Disabled
import quest.laxla.spock.math.Vectorization.Kind.Exact

/**
 * @since 0.0.1-alpha.4
 * @see FloatSpace
 * @see DoubleSpace
 */
public interface FractionalSpace<@Vectorization(kind = Exact) V> : NumeralSpace<V> where V : Comparable<V> {
	public infix fun V.pow(@Vectorization(kind = Disabled) exponent: V): V
	
	public fun V.sqrt(): V
}
