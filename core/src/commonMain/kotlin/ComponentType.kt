package quest.laxla.spock

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass


/**
 * An [Entity] whose [dataType] is known.
 *
 * Implementations of [Ecs] may (and on debug mode, **must**) throw when given a [ComponentType] of the wrong [dataType].
 *
 * It is advised to replace references to `ComponentType<*>` with [Entity],
 * as the former implements the latter and dereferencing [NamedEntity]s into [Entity.Reference]s
 * is far cheaper than the other way around; That being said,
 * acquiring a reference is more always expensive than simply passing the [ComponentType],
 * so avoid excessive [reference] calls.
 *
 * @since 0.0.1-alpha.4
 */
@Serializable
public sealed interface ComponentType<T> : Entity {
	/**
	 * The type of data this entity carries if added to a [Compable].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val dataType: KClass<T & Any>

	/**
	 * The default value for components of this type.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val defaultDefault: T

	/**
	 * Will [MutableEcs.mutate] functions that take more than the mutated entity ignore entities without this component?
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val onlyMutateSetComponentsByDefault: Boolean get() = false

	/**
	 * An entity that does not carry any data. Simply, a marker.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public typealias Tag = ComponentType<Unit>

	/**
	 * Nameless, dataless [ComponentType].
	 *
	 * This class is not the same as an [Entity.Reference];
	 * The former may refer to any entity by its ID,
	 * while this one is used to mark [ComponentType]s whose [dataType]s are [Unit] - aka, a [Tag].
	 * Implementations may throw when given the wrong
	 *
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	@Serializable
	public value class Autonomous(public override val id: UInt) : Tag, Entity.Opaque {
		override val dataType: KClass<Unit> get() = Unit::class
		override val defaultDefault: Unit get() = Unit

		override fun toString(): String = "Tag($formattedID)"
	}
}
