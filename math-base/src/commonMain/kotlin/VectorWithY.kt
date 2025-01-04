package quest.laxla.spock.math

/**
 * Vector with two or more elements.
 * 
 * @since 0.0.1-alpha.4
 * @see Vector2
 */
public interface VectorWithY<V, out S> : VectorWithX<V, S> where S : Space<V> {
	/**
	 * The second element of this vector.
	 * 
	 * @since 0.0.1-alpha.4
	 */
	public val y: V
}
