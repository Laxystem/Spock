package quest.laxla.spock.math

/**
 * Vector with four or more elements.
 * 
 * @since 0.0.1-alpha.4
 * @see Vector4
 */
public interface VectorWithW<V, out S> : VectorWithZ<V, S> where S : Space<V> {
	/**
	 * The fourth element of this vector.
	 * 
	 * @since 0.0.1-alpha.4
	 */
	public val w: V
}