package quest.laxla.spock.math

import quest.laxla.spock.FutureValueClass

/**
 * Vector with exactly one element, [x].
 *
 * This class should not be used, and is only provided for the vector creation DSL.
 *
 * @since 0.0.1-alpha.4
 * @see Space.x
 * @see UniformVector.asVector1
 */
@FutureValueClass
public interface Vector1<V, out S> : VectorWithX<V, S> where S : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object
}
