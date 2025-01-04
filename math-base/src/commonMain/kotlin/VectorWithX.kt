package quest.laxla.spock.math

/**
 * Vector with one or more element.
 * 
 * @since 0.0.1-alpha.4
 * @see Vector1
 */
public interface VectorWithX<V, out S> : Vector<V, S> where S : Space<V> {
	/**
	 * The first element of this vector.
	 * 
	 * @since 0.0.1-alpha.4
	 */
	public val x: V
}
