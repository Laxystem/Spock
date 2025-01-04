package quest.laxla.spock.math

/**
 * Vector with exactly four elements, [x], [y], [z], and [w].
 * 
 * @since 0.0.1-alpha.4
 */
public interface Vector4<V, out S> : VectorWithW<V, S> where S : Space<V>