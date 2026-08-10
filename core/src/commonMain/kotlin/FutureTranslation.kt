package quest.laxla.spock.core

import kotlin.annotation.AnnotationTarget.TYPEALIAS

/**
 * Temporary annotation indicating the name of a type or property in another language.
 *
 * Will be replaced with a proper translation system in the future,
 * and only exists to remember the names thought of at a class' creation.
 *
 * Contributors, feel free to add additional empty [String]-defaulting fields.
 *
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, TYPEALIAS)
public annotation class FutureTranslation(public val hebrew: String = "")
