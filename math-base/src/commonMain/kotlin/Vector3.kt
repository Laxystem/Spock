package quest.laxla.spock.math

/**
 * Vector with exactly three elements, [x], [y], and [z].
 * 
 * @since 0.0.1-alpha.4
 */
public interface Vector3<V, out S> : VectorWithZ<V, S> where S : Space<V>