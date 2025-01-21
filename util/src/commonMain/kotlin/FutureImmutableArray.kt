package quest.laxla.spock

/**
 * Marks that this [ImmutableList][kotlinx.collections.immutable.ImmutableList] will be replaced by an immutable array
 * when supported on Kotlin/Multiplatform.
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Target(AnnotationTarget.TYPE)
public annotation class FutureImmutableArray
