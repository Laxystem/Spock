package quest.laxla.spock.math

/**
 * Marks this function as a vector operation.
 *
 * Spock Math will codegen extension functions for vectors of different element counts, for example:
 * ```kotlin
 * @VectorOperation
 * fun <@Vectorization(kind = Exact) T> T.foo(other: T): T
 * // will generate for every vector size...
 * fun <T> Vector3<T>.foo(other: Vector3<T>): Vector3<T>
 * ```
 *
 * @since 0.0.1-alpha.4
 * @see Vectorization
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class VectorOperation
