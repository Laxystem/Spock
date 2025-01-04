package quest.laxla.spock.math

/**
 * Vector with three or more elements.
 * 
 * @since 0.0.1-alpha.4
 * @see Vector3
 */
public interface VectorWithZ<V, out S> : VectorWithY<V, S> where S : Space<V> {
	/**
	 * The third element of this vector.
	 * 
	 * @since 0.0.1-alpha.4
	 */
	public val z: V
}