package quest.laxla.spock

/**
 * Marks lower-level API that should not be used unless you know what you're doing.
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@RequiresOptIn("Lower-level API should not be used unless you know what you're doing.",)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class RawSpockApi
