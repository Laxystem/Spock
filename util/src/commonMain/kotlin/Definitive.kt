package quest.laxla.spock

/**
 * Marks data and value classes that have reached their definitive form,
 * and won't gain additional properties in the future.
 * 
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
public annotation class Definitive(val since: String)
