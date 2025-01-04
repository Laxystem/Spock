package quest.laxla.spock.math

/**
 * Stores a collection of a constant number of objects of the same type,
 * and the operations that can be invoked on them.
 * 
 * @since 0.0.1-alpha.4
 */
public interface Vector<V, out S> where S : Space<V> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val space: S
}
