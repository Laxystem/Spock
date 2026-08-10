package quest.laxla.spock.ecs

import kotlinx.serialization.Serializable

/**
 * Represents the [possessive] having a component of type [possessed].
 *
 * Possessions describe *potential* relationships, not necessarily actual ones.
 * Hence, they may be [materialize]d or [delete]d.
 *
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
@Serializable
public data class Possession<C : Compable, T>(val possessive: C, val possessed: ComponentType<T>) : Compable
