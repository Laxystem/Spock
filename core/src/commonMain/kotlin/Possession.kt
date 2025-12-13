package quest.laxla.spock

/**
 * Represents the [possessive] having a component of type [possessed].
 *
 * Possessions describe *potential* relationships, not necessarily actual ones.
 * Hence, they may be [materialize]d or [delete]d.
 *
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public data class Possession<C : Compable, T>(val possessive: C, val possessed: Entity<T>) : Compable
