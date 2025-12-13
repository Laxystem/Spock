package quest.laxla.spock

import kotlin.jvm.JvmInline

/**
 * Represents an entity.
 *
 * Despite entities and component types being one and the same in Spock ECS,
 * it is often more performant to refer to [NamedEntity]s by their IDs,
 * as dereferencing [name][Named.name]s require additional hashing.
 *
 * @since 0.0.1-alpha.4
 */
public sealed interface Entity : Compable {
	/**
	 * An [Entity] whose [dataType][ComponentType.dataType] is not known.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	public value class Reference(public val id: UInt) : Entity
}
