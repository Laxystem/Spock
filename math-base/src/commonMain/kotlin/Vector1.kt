package quest.laxla.spock.math

/**
 * Vector with exactly one element, [x].
 * 
 * This class should not be used, and is only provided for the vector creation DSL. 
 * 
 * @since 0.0.1-alpha.4
 * @see Space.x
 * @see ConstantVector.asVector1
 */
public interface Vector1<V, out S> : VectorWithX<V, S> where S : Space<V>
