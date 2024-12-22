package quest.laxla.spock

import kotlin.annotation.Target

/**
 * Marks experimental API that is not final and subject to change.
 * 
 * @since 0.0.1-alpha.1
 */
@RequiresOptIn
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_SETTER)
public annotation class ExperimentalSpockApi
