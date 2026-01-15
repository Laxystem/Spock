package quest.laxla.spock

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSynthetic

/**
 * @author Laxystem
 */
private fun UInt.formatID() = '#' + toHexString(HexFormat.UpperCase)

/**
 * Provides a uniform error for using a foreign [Entity].
 *
 * @since 0.0.1-alpha.4
 */
public fun unknownEntity(entity: UInt, message: String = emptyString()): Nothing = throw IllegalArgumentException(
	message.trimAndAppendIfNotBlank(suffix = ": ") +
			"Unknown entity ${entity.formatID()} does not belong to this ECS."
)

/**
 * Provides a uniform error for using a foreign [Entity].
 *
 * @since 0.0.1-alpha.4
 */
public fun unknownEntity(entity: Entity.Reference, message: String = emptyString()): Nothing =
	unknownEntity(entity.id, message)

/**
 * Generates a canonical name by which this entity can be *identified* in UIs.
 *
 *
 * @since 0.0.1-alpha.4
 */
public val Compable.formattedID: String
	get() = when (this) {
		is Possession<*, *> -> possessive.formattedID + '/' + possessed.formattedID
		is NamedEntity<*> -> name.toString()
		is Entity.Opaque -> id.formatID()
	}

/**
 * Retrieves the [UInt] ID associated with this [entity],
 * or [UInt.MAX_VALUE] if none.
 *
 * @since 0.0.1-alpha.4
 * @see ComponentType.Autonomous.id
 */
public fun Ecs.reference(entity: Entity): Entity.Reference = when (entity) {
	is Entity.Reference -> entity
	is Entity.Opaque -> Entity.Reference(entity.id)
	is NamedEntity<*> -> reference(entity.name)
}

/**
 * Retrieves the [ComponentType] this opaque [entity] refers to, or throws if it does not exist.
 *
 * @since 0.0.1-alpha.4
 */
public fun Ecs.dereferenceOrThrow(
	entity: Entity.Reference,
	message: String = "Could not dereference entity"
): ComponentType<*> = dereference(entity) ?: unknownEntity(entity, message)

/*
/**
 * The default value for [this] component type within this [ecs], overruling [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public suspend fun <T> ComponentType<T>.default(): T = ecs.getDefaultOf(this)

private const val defaultPropertyError = "Use suspend function, not property"

TODO: dokka fails compilation
/**
 * The default value for [this] component type within this [ecs], overruling [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public var <T> ComponentType<T>.default: T
	set(value) = ecs.setDefaultOf(this, value)
	@Deprecated(defaultPropertyError, ReplaceWith("this.default()"), DeprecationLevel.ERROR)
	@JvmSynthetic
	get() = throw NotImplementedError(defaultPropertyError)
 */

/**
 * Resets [ComponentType.default] to [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun Entity.resetDefault() {
	ecs.resetDefaultOf(this)
}

/**
 * Iterates over components of [this] provided type, running a lambda [forEach] one.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public fun <T, R> ComponentType<T>.select(forEach: suspend Entity.Selected.(T) -> R): Flow<R> =
	ecs.select(componentType = this, forEach)

/**
 * Iterates over every entity with a component of the provided type,
 * and applies the provided transformation to it.
 *
 * This function always behaves as if `onlyMutateSetComponents` is true,
 * as it cannot query entities otherwise.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T> ComponentType<T>.mutate(crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T) -> T): Job =
	context(MutableEcs.Selection) {
		ecs.mutate(componentType = this) {
			forEach(it)
		}
	}

/**
 * Iterates over every entity with a component of both provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T, T2> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T, T2) -> T
): Job = context(MutableEcs.Selection) {
	ecs.mutate(mutatedComponentType = this, componentType2, onlyMutateSetComponents) { t, t2 ->
		forEach(t, t2)
	}
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T, T2, T3> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T, T2, T3) -> T
): Job = context(MutableEcs.Selection) {
	ecs.mutate(mutatedComponentType = this, componentType2, componentType3, onlyMutateSetComponents) { t, t2, t3 ->
		forEach(t, t2, t3)
	}
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T, T2, T3, T4> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T, T2, T3, T4) -> T
): Job = context(MutableEcs.Selection) {
	ecs.mutate(
		mutatedComponentType = this,
		componentType2,
		componentType3,
		componentType4,
		onlyMutateSetComponents
	) { t, t2, t3, t4 ->
		forEach(t, t2, t3, t4)
	}
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T, T2, T3, T4> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	vararg componentTypes: Entity,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T, T2, T3, T4) -> T
): Job = context(MutableEcs.Selection) {
	ecs.mutate(
		mutatedComponentType = this,
		componentType2,
		componentType3,
		componentType4,
		componentTypes = componentTypes,
		onlyMutateSetComponents,
	) { t, t2, t3, t4 ->
		forEach(t, t2, t3, t4)
	}
}

/**
 * Iterates over every entity with a component of the provided type,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T, R> ComponentType<R>.set(
	componentType: ComponentType<T>,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T) -> R
): Job = context(MutableEcs.Selection) {
	ecs.set(modifiedComponentType = this, componentType = componentType) {
		forEach(it)
	}
}

/**
 * Iterates over every entity with a component of both provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T1, T2, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T1, T2) -> R
): Job = context(MutableEcs.Selection) {
	ecs.set(modifiedComponentType = this, componentType1, componentType2) { t1, t2 ->
		forEach(t1, t2)
	}
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T1, T2, T3, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T1, T2, T3) -> R
): Job = context(MutableEcs.Selection) {
	ecs.set(modifiedComponentType = this, componentType1, componentType2, componentType3) { t1, t2, t3 ->
		forEach(t1, t2, t3)
	}
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T1, T2, T3, T4, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T1, T2, T3, T4) -> R
): Job = context(MutableEcs.Selection) {
	ecs.set(
		modifiedComponentType = this,
		componentType1,
		componentType2,
		componentType3,
		componentType4
	) { t1, t2, t3, t4 ->
		forEach(t1, t2, t3, t4)
	}
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public inline fun <T1, T2, T3, T4, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	vararg componentTypes: Entity,
	crossinline forEach: suspend context(MutableEcs.Selection) Entity.Selected.(T1, T2, T3, T4) -> R
): Job = context(MutableEcs.Selection) {
	ecs.set(
		modifiedComponentType = this,
		componentType1,
		componentType2,
		componentType3,
		componentType4,
		componentTypes = componentTypes,
	) { t1, t2, t3, t4 ->
		forEach(t1, t2, t3, t4)
	}
}

/**
 * Checks if this [Compable] exists.
 * * If [this] is an [ComponentType], returns whether it is found at the root of this [Ecs].
 * * If [this] is a [Possession], returns whether the described component exists.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public suspend fun Compable.exists(): Boolean = this in ecs

/**
 * [Add][MutableEcs]s this [NamedEntity] onto the Entity-Component-System.
 *
 * This is a **non-terminal** setter.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun NamedEntity<*>.materialize() {
	ecs.add(this)
}

/**
 * Checks if [this] entity has a component of the provided type.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public suspend operator fun Compable.contains(componentType: Entity): Boolean =
	ecs.possesses(entity = this, componentType)

/**
 * [Get][Ecs.get]s the component of the provided type from [this] entity, or the default value if not found.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public suspend operator fun <T> Compable.get(componentType: ComponentType<T>): T = ecs[this, componentType]

/**
 * [Set][MutableEcs.set]s the component of the provided type on [this] entity to [value].
 *
 * This is a **terminal** setter;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public operator fun <T> Compable.set(componentType: ComponentType<T>, value: T) {
	ecs[this, componentType] = value
}

/**
 * [Materialize][MutableEcs.materialize]s a component of the provided type onto [this] entity.
 *
 * This is a **non-terminal** setter, as its contract is "ensure entity possesses component";
 * The implementation is free to choose any value given multiple concurrent invocations.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public operator fun Compable.plusAssign(componentType: Entity) {
	ecs.materialize(entity = this, componentType)
}

/**
 * If [this] entity already has a component of the provided type, [remove][MutableEcs.remove]s it;
 * Else, adds a default one.
 *
 * This is a **terminal** setter, as toggling is an inherently thread-unsafe operation;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 * @see MutableEcs.setToDefault
 */
context(ecs: MutableEcs)
public suspend fun Compable.toggle(componentType: Entity) {
	ecs.toggle(entity = this, componentType = componentType)
}

/**
 * [Removes][MutableEcs.remove] the component of the provided type from [this] entity.
 *
 * This is a **terminal setter**; Unlike [Possession.unset],
 * concurrent setting of this component to another value is considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi // TODO: decide if unset or remove
context(ecs: MutableEcs)
public operator fun Compable.minusAssign(componentType: Entity) {
	ecs.remove(entity = this, componentType)
}

/**
 * Removes this [Compable] and all its components.
 * * If [this] is an [ComponentType], all components of its associated type will be removed as well.
 * * If [this] is a [Possession], the described component will be [remove][MutableEcs.remove]d if it exists.
 *
 * @since 0.0.1-alpha.
 */
context(ecs: MutableEcs)
public fun Compable.delete() {
	ecs.delete(campable = this)
}

/**
 * Creates a [Possession], with the receiver as the [Possession.possessive].
 *
 * @since 0.0.1-alpha.4
 */
context(_: Ecs)
public operator fun <C : Compable, T> C.div(possessed: ComponentType<T>): Possession<C, T> =
	Possession(possessive = this, possessed)

/**
 * Retrieves the component described by this [Possession],
 * or the default value if not found.
 *
 * @since 0.0.1-alpha.4
 */
context(_: Ecs)
public suspend fun <T> Possession<*, T>.get(): T = possessive[possessed]

/**
 * Forgets this possession has existed, thus [unsetting][MutableEcs.unset] it.
 *
 * This is a **non-terminal** setter; Unlike [Possession.delete],
 * the implementation is free to select how to behave when the described component is concurrently set to another value,
 * but the current one (as would be returned by [get]) is guaranteed to be forgotten.
 *
 * @since 0.0.1-alpha.4
 * @see Compable.minusAssign
 */
context(ecs: MutableEcs)
public fun Possession<*, *>.unset() {
	ecs.unset(entity = possessive, componentType = possessed)
}

/**
 * [Assign][MutableEcs.set]s [value] to be the described component, overriding previous values.
 *
 * This is a **terminal setter**;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T> Possession<*, T>.assign(value: T) {
	this[possessed] = value
}

/**
 * If the described component does not exist, [adds][MutableEcs.setIfNotPresent] this [value] as such.
 *
 * This is a **non-terminal setter**;
 * The implementation is free to choose any value given multiple concurrent invocations.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T> Possession<*, T>.assignIfNotPresent(value: T) {
	ecs.setIfNotPresent(entity = this, componentType = possessed, value)
}

/**
 * Assigns the default value to the described component.
 *
 * This is a **terminal setter**;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 * @see MutableEcs.setToDefault
 */
context(ecs: MutableEcs)
public fun Possession<*, *>.assignDefault() {
	ecs.setToDefault(entity = possessive, componentType = possessed)
}

/**
 * [Materialize][MutableEcs.materialize]s the described component.
 *
 * This is a **non-terminal setter**;
 * The implementation is free to choose any value given multiple concurrent invocations.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun Possession<*, *>.materialize() {
	ecs.materialize(entity = possessive, componentType = possessed)
}

/**
 * If the described component exists, [delete]s it;
 * Else, [adds the default][MutableEcs.setToDefault].
 *
 * This is a **terminal** setter, as toggling is an inherently thread-unsafe operation;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 * @see assignDefault
 */
context(ecs: MutableEcs)
public suspend fun Possession<*, *>.toggle() {
	possessive.toggle(componentType = possessed)
}

/**
 * [DslMarker] for [Ecs] and [MutableEcs].
 *
 * @since 0.0.1-alpha.4
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
public annotation class EcsDsl
