package quest.laxla.spock

/**
 * Marks that when supported by Kotlin,
 * * This property will become `suspend`;
 * * This function will become `suspend operator fun getValue`;
 * * Or that this class, object or interface will inherit `SuspendReadOnlyProperty` instead of `suspend () -> T`.
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class FutureSuspendProperty
