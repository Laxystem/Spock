package quest.laxla.spock

/**
 * Marks functions that'll be extracted from their current type into context functions
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class FutureContextFunction
