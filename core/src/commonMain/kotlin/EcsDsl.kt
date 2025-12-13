package quest.laxla.spock

/**
 * Provides a uniform error for using a foreign [Entity].
 *
 * @since 0.0.1-alpha.4
 */
public fun unknownEntity(entity: UInt, message: String = emptyString()): Nothing = throw IllegalArgumentException(
	message.trimAndAppendIfNotBlank(suffix = ": ") +
			"Unknown entity ${entity.toHexString(HexFormat.UpperCase)} does not belong to this ECS."
)

/**
 * Provides a uniform error for using a foreign [Entity].
 *
 * @since 0.0.1-alpha.4
 */
public fun unknownEntity(entity: Entity.Reference, message: String = emptyString()): Nothing =
	unknownEntity(entity.id, message)

/**
 * Retrieves the [UInt] ID associated with this [entity],
 * or [UInt.MAX_VALUE] if none.
 *
 * @since 0.0.1-alpha.4
 * @see ComponentType.Autonomous.id
 */
public fun Ecs.reference(entity: Entity): Entity.Reference = when (entity) {
	is Entity.Reference -> entity
	is ComponentType.Autonomous -> Entity.Reference(entity.id)
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

/**
 * The default value for [this] component type within this [ecs], overruling [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)

public val <T> ComponentType<T>.default: T get() = ecs.getDefaultOf(this)

/**
 * The default value for [this] component type within this [ecs], overruling [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
@Suppress("CONTEXTUAL_OVERLOAD_SHADOWED") // same behavior
context(ecs: MutableEcs)
public var <T> ComponentType<T>.default: T
	get() = ecs.getDefaultOf(this)
	set(value) = ecs.setDefaultOf(this, value)

context(ecs: MutableEcs)

/**
 * Resets [ComponentType.default] to [ComponentType.defaultDefault].
 *
 * @since 0.0.1-alpha.4
 */
public fun Entity.resetDefault() {
	ecs.resetDefaultOf(this)
}

/**
 * Iterates over components of [this] provided type, running a lambda [forEach] one.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public fun <T> ComponentType<T>.select(forEach: context(Ecs) Compable.(T) -> Unit) {
	ecs.select(componentType = this, forEach)
}

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
public fun <T> ComponentType<T>.mutate(forEach: context(Ecs) Compable.(T) -> T) {
	ecs.mutate(componentType = this, forEach)
}

/**
 * Iterates over every entity with a component of both provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T, T2> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	forEach: context(Ecs) Compable.(T, T2) -> T
) {
	ecs.mutate(mutatedComponentType = this, componentType2, onlyMutateSetComponents, forEach)
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T, T2, T3> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	forEach: context(Ecs) Compable.(T, T2, T3) -> T
) {
	ecs.mutate(mutatedComponentType = this, componentType2, componentType3, onlyMutateSetComponents, forEach)
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T, T2, T3, T4> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	forEach: context(Ecs) Compable.(T, T2, T3, T4) -> T
) {
	ecs.mutate(
		mutatedComponentType = this,
		componentType2,
		componentType3,
		componentType4,
		onlyMutateSetComponents,
		forEach
	)
}

/**
 * Iterates over every entity with a component of all provided types,
 * and applies the provided transformation to the first.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T, T2, T3, T4> ComponentType<T>.mutate(
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	vararg componentTypes: Entity,
	onlyMutateSetComponents: Boolean = onlyMutateSetComponentsByDefault,
	forEach: context(Ecs) Compable.(T, T2, T3, T4) -> T
) {
	ecs.mutate(
		mutatedComponentType = this,
		componentType2,
		componentType3,
		componentType4,
		componentTypes = componentTypes,
		onlyMutateSetComponents,
		forEach
	)
}

/**
 * Iterates over every entity with a component of the provided type,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T, R> ComponentType<R>.set(componentType: ComponentType<T>, forEach: context(Ecs) Compable.(T) -> R) {
	ecs.set(modifiedComponentType = this, componentType, forEach)
}

/**
 * Iterates over every entity with a component of both provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T1, T2, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	forEach: context(Ecs) Compable.(T1, T2) -> R
) {
	ecs.set(modifiedComponentType = this, componentType1, componentType2, forEach)
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T1, T2, T3, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	forEach: context(Ecs) Compable.(T1, T2, T3) -> R
) {
	ecs.set(modifiedComponentType = this, componentType1, componentType2, componentType3, forEach)
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T1, T2, T3, T4, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	forEach: context(Ecs) Compable.(T1, T2, T3, T4) -> R
) {
	ecs.set(modifiedComponentType = this, componentType1, componentType2, componentType3, componentType4, forEach)
}

/**
 * Iterates over every entity with a component of all provided component types,
 * and sets the result onto each.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: MutableEcs)
public fun <T1, T2, T3, T4, R> ComponentType<R>.set(
	componentType1: ComponentType<T1>,
	componentType2: ComponentType<T2>,
	componentType3: ComponentType<T3>,
	componentType4: ComponentType<T4>,
	vararg componentTypes: Entity,
	forEach: context(Ecs) Compable.(T1, T2, T3, T4) -> R
) {
	ecs.set(
		modifiedComponentType = this,
		componentType1,
		componentType2,
		componentType3,
		componentType4,
		componentTypes = componentTypes,
		forEach
	)
}

/**
 * Checks if this [Compable] exists.
 * * If [this] is an [ComponentType], returns whether it is found at the root of this [Ecs].
 * * If [this] is a [Possession], returns whether the described component exists.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public fun Compable.exists(): Boolean = this in ecs

private const val materializeErrorMessage = "Failed materializing entity"

/**
 * Adds this [Compable] if it does not already exist.
 * * If [this] is a [NamedEntity], [add][MutableEcs.add]s it onto the ECS.
 * * If [this] is a [Possession], [materializes][MutableEcs.materialize]s it per usual.
 * * If [this] is an [ComponentType.Autonomous], throws if it does not exist.
 * * If [this] is an [Entity.Reference], [dereferences][Ecs.dereference] it then acts per above.
 *
 * This is a **non-terminal** setter.
 *
 * @since 0.0.1-alpha.4
 */
@Throws(IllegalArgumentException::class)
context(ecs: MutableEcs)
public fun Compable.materialize() {
	when (this) {
		is NamedEntity<*> -> ecs.add(this)
		is Possession<*, *> -> ecs.materialize(entity = possessive, componentType = possessed)
		is Entity.Reference -> ecs.dereferenceOrThrow(this, materializeErrorMessage).materialize()
		is ComponentType.Autonomous -> if (!exists()) unknownEntity(id, materializeErrorMessage)
	}
}

/**
 * Checks if [this] entity has a component of the provided type.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public operator fun Compable.contains(componentType: Entity): Boolean =
	ecs.possesses(entity = this, componentType)

/**
 * [Get][Ecs.get]s the component of the provided type from [this] entity, or the default value if not found.
 *
 * @since 0.0.1-alpha.4
 */
context(ecs: Ecs)
public operator fun <T> Compable.get(componentType: ComponentType<T>): T = ecs[this, componentType]

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
public fun Compable.toggle(componentType: Entity) {
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
@ExperimentalSpockApi
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
public fun <T> Possession<*, T>.get(): T = possessive[possessed]

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
 * If the described component exists, [delete]s it;
 * Else, [materialize][MutableEcs.materialize]s it.
 *
 * This is a **terminal** setter, as toggling is an inherently thread-unsafe operation;
 * Multiple concurrent invocations are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 * @see assignDefault
 */
context(ecs: MutableEcs)
public fun Possession<*, *>.toggle() {
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
