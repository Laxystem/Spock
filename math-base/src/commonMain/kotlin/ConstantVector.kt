package quest.laxla.spock.math

/**
 * Vector whose elements are all equal.
 *
 * @since 0.0.1-alpha.4
 * @see asVector1
 * @see asVector2
 * @see asVector3
 * @see asVector4
 */
public interface ConstantVector<V, out S> : Vector<V, S> where S : Space<V> {
	public val value: V
}
