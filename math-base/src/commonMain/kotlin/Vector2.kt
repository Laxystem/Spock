package quest.laxla.spock.math

import quest.laxla.spock.FutureValueClass

/**
 * Vector with exactly two elements, [x] and [y].
 *
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public interface Vector2<V, out S> : VectorWithY<V, S> where S : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object
}
