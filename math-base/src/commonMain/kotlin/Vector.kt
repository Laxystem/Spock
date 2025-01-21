package quest.laxla.spock.math

import quest.laxla.spock.FutureValueClass

/**
 * Stores a constant number of objects of the same type (usually referred to as "elements"),
 * and a [space] providing operations that can be invoked over said elements.
 * 
 * Vectors are deeply immutable and thread-safe.
 * 
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public interface Vector<V, out S> where S : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 * @see Space
	 */
	public val space: S
}
