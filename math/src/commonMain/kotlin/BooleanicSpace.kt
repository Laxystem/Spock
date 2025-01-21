package quest.laxla.spock.math

import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.Vectorization.Kind.Exact

/**
 * @since 0.0.1-alpha.4
 */
@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface BooleanicSpace<@Vectorization(kind = Exact) V> : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 * @see Boolean.not
	 */
	@VectorOperation
	public operator fun V.not(): V

	/**
	 * @since 0.0.1-alpha.4
	 * @see Boolean.and
	 */
	@VectorOperation
	public infix fun V.and(other: V): V

	/**
	 * @since 0.0.1-alpha.4
	 * @see Boolean.or
	 */
	@VectorOperation
	public infix fun V.or(other: V): V

	/**
	 * @since 0.0.1-alpha.4
	 * @see Boolean.xor
	 */
	@VectorOperation
	public infix fun V.xor(other: V): V
}
