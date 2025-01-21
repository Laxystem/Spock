package quest.laxla.spock.math

import quest.laxla.spock.FutureValueClass

/**
 * Vector with exactly three elements, [x], [y], and [z].
 * 
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public interface Vector3<V, out S> : VectorWithZ<V, S> where S : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object
}
