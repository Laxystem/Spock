package quest.laxla.spock.math

import quest.laxla.spock.math.Vectorization.Kind.Exact

public interface BooleanicSpace<@Vectorization(kind = Exact) V> : Space<V> {
	@VectorOperation
	public infix fun V.or(other: V): V

	@VectorOperation
	public infix fun V.and(other: V): V

	@VectorOperation
	public operator fun V.not(): V

	@VectorOperation
	public infix fun V.xor(other: V): V
}
