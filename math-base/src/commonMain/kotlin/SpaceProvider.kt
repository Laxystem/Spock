package quest.laxla.spock.math

/**
 * Marks a [VectorOperation] parameter of non-[Disabled][Vectorization.Kind.Exact] [Vectorization]
 * to provide the [space][Vector.space] for the output vector.
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class SpaceProvider
