package quest.laxla.spock.math

import quest.laxla.spock.math.Vectorization.Kind.Disabled
import quest.laxla.spock.math.Vectorization.Kind.Exact

public interface NumeralSpace<@Vectorization(kind = Exact) V> : Space<V> where V : Comparable<V> {
	@VectorOperation
	public operator fun V.minus(other: V): V

	@VectorOperation
	public operator fun V.plus(other: V): V

	@VectorOperation
	public operator fun V.times(@Vectorization(kind = Disabled) other: V): V

	@VectorOperation
	public operator fun V.div(@Vectorization(kind = Disabled) other: V): V
	
	@VectorOperation
	public operator fun V.rem(@Vectorization(kind = Disabled) other: V): V
}
