package quest.laxla.spock

import kotlinx.serialization.Serializable
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
@Serializable
public sealed interface Entity : Compable {
	/**
	 * Implemented by any kind of [Entity] that is directly referred to by [id].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Serializable
	public sealed interface Opaque : Entity {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public val id: UInt
	}

	/**
	 * An [Entity] whose [dataType][ComponentType.dataType] is not known.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	@Serializable
	public value class Reference(public override val id: UInt) : Opaque {
		override fun toString(): String = formattedID
	}

	/**
	 * An [Entity] whose [ComponentType.dataType] is not known, to be used during ECS [selection][Ecs.select].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	@Serializable
	public value class Selected(public override val id: UInt) : Opaque {
		/**
		 * Thrown inside [MutableEcs.Selection]s to delete the current entity.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public class DeletionException(entity: Selected, cause: Throwable?) : MutableEcs.Selection.DataFlowException(entity, cause)

		/**
		 * Deletes the currently selected entity.
		 *
		 * @since 0.0.1-alpha.4
		 */
		context(_: MutableEcs.Selection)
		public fun delete(cause: Throwable? = null): Nothing = throw DeletionException(this, cause)

		/**
		 * Thrown inside [MutableEcs.Selection]s to remove the relevant component from the current entity.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public class ComponentRemovalException(
			entity: Selected,
			cause: Throwable?,
			public val terminal: Boolean
		) : MutableEcs.Selection.DataFlowException(entity, cause, message = "terminal=$terminal")

		/**
		 * Removes the mutated component from the currently selected entity.
		 *
		 * @since 0.0.1-alpha.4
		 */
		context(_: MutableEcs.Selection)
		public fun remove(cause: Throwable? = null): Nothing = throw ComponentRemovalException(this, cause, true)

		/**
		 * Unsets the mutated component from the currently selected entity.
		 *
		 * @since 0.0.1-alpha.4
		 */
		context(_: MutableEcs.Selection)
		public fun unset(cause: Throwable? = null): Nothing = throw ComponentRemovalException(this, cause, false)

		override fun toString(): String = "$formattedID (selected)"
	}
}
