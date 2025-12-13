package quest.laxla.spock

/**
 * Storage for an entity-component-system.
 *
 * Spock ECS-es map [Compable]s - that is, an [Entity] or a component added to one - to components,
 * with those being a value associated with a component type which is, too, an [Entity].
 * * Not all component types are associated with a [data type][Entity.dataType] - those are called [Tag][Entity.Tag]s.
 * * Entities are simply an identifier, that being a [Name] (a [NamedEntity]),
 *   or an unsigned 32-bit [id][Entity.Autonomous.id].
 *   The second type, also called an [Autonomous][Entity.Autonomous], is the most common.
 *
 * All functions declared in this interface are considered to be *getter methods*.
 *
 * @since 0.0.1-alpha.4
 * @see SnappedEcs
 * @see MutableEcs
 */
@EcsDsl
public interface Ecs {
    /**
     * Retrieves the [UInt] ID associated with this [name], or [UInt.MAX_VALUE] if none.
     *
     * @since 0.0.1-alpha.4
     * @see Entity.Autonomous.id
     */
    public fun idOf(name: Name): UInt

    /**
     * Retrieves the component of the provided type associated with this [entity], or the default value if not found.
     *
     * @since 0.0.1-alpha.4
     * @see MutableEcs.getAndSet
     * @see MutableEcs.getOrSet
     * @see MutableEcs.getAndSetToDefault
     * @see MutableEcs.getAndMaterialize
     * @see MutableEcs.getAndRemove
     */
    public operator fun <T> get(entity: Compable, componentType: Entity<T>): T

    /**
     * Iterates over entities with components of all provided types,
     * running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun select(vararg componentTypes: Entity<*>, forEach: Compable.() -> Unit)

    /**
     * Iterates over components of the provided type, running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T> select(componentType: Entity<T>, forEach: context(Ecs) Compable.(T) -> Unit)

    /**
     * Iterates over entities with components of both provided types,
     * running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2> select(
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        forEach: context(Ecs) Compable.(T1, T2) -> Unit
    )

    /**
     * Iterates over entities with components of all provided types,
     * running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3> select(
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        forEach: context(Ecs) Compable.(T1, T2, T3) -> Unit
    )

    /**
     * Iterates over entities with components of all provided types,
     * running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3, T4> select(
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        forEach: context(Ecs) Compable.(T1, T2, T4) -> Unit
    )

    /**
     * Iterates over entities with components of all provided types,
     * running a lambda [forEach] one.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3, T4> select(
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        vararg componentTypes: Entity<*>,
        forEach: context(Ecs) Compable.(T1, T2, T3, T4) -> Unit
    )

    /**
     * Checks if this [entity] has a component of the provided type.
     *
     * This is the same as executing [contains] with a [Possession].
     *
     * @since 0.0.1-alpha.4
     * @see MutableEcs.toggle
     * @see MutableEcs.materialize
     */
    public fun possesses(entity: Compable, componentType: Entity<*>): Boolean

    /**
     * Checks if the described [compable] exists.
     *
     * If it is an [Entity], returns whether said entity is found at the root of this [Ecs].
     * If it is a [Possession], returns the same as [possesses].
     *
     * @since 0.0.1-alpha.4
     * @see MutableEcs.materialize
     * @see MutableEcs.delete
     */
    public operator fun contains(compable: Compable): Boolean

    /**
     * Retrieves the default value for this [componentType].
     *
     * @since 0.0.1-alpha.4
     * @see MutableEcs.setDefaultOf
     * @see MutableEcs.getAndSetDefaultOf
     * @see MutableEcs.resetComponentTypeDefaults
     */
    public fun <T> getDefaultOf(componentType: Entity<T>): T

    /**
     * Creates a read-only, deeply-immutable [snap][SnappedEcs] of the current state of this Entity-Component-System.
     *
     * ***Warning:** this is an expensive operation.*
     * @since 0.0.1-alpha.4
     */
    public fun snap(): SnappedEcs
}

/**
 * An [Ecs] whose *getter* methods are read-only, and return a constant value.
 *
 * Snapped entity component systems may still have setter methods by implementing [MutableEcs],
 * but even those will not affect getters' return values.
 *
 * @since 0.0.1-alpha.4
 */
public interface SnappedEcs : Ecs

/**
 * An [Ecs] whose values may be modified.
 *
 * This interface is thread-safe but not `suspend`, thus requiring atomic operations to be used;
 * There's no guarantee whatsoever the effects of a setter method will persist into a getter method.
 *
 * All functions defined in this interface are setter methods.
 * Each has:
 * * Two variants, one returning the old value and one not.
 * * **Terminality.**
 *    * For **terminal** setter methods, the current value will be overridden if there already is one,
 *      and multiple concurrent invocations are considered undefined behaviour.
 *    * For **non-terminal** setter methods, an existing value *won't* be overridden (except for delete methods),
 *      and in the case of multiple concurrent invocations, the implementation may instead choose a value arbitrarily;
 *      Note for returning variants, the value relevant to each call will be returned and not the final one.
 * * An additional variant using the default value ("clear methods").
 * * And another removing the component altogether ("delete methods").
 *
 * | Method | Terminal, non-Returning | Terminal, Returning  | non-Terminal, non-Returning | non-Terminal, Returning |
 * |-------:|-------------------------|----------------------|-----------------------------|-------------------------|
 * |    Get | <small class="na"/>     | <small class="na"/>  | <small class="na"/>         | [get]                   |
 * |    Set | [set]                   | [getAndSet]          | [setIfNotPresent]           | [getOrSet]              |
 * |  Clear | [setToDefault]          | [getAndSetToDefault] | [materialize]               | [getAndMaterialize]     |
 * | Delete | [remove]                | [getAndRemove]       | [unset]                     | [getAndUnset]           |
 * | Toggle | [toggle]                | <small class="na"/>  | <small class="na"/>         | <small class="na"/>     |
 * All relevant variants will be listed in the same order per above in the "see also" section of the function's documentation.
 *
 * ## Context Extensions
 *
 * | [Compable] extensions | Terminal                   | non-Terminal                    |
 * |----------------------:|----------------------------|---------------------------------|
 * |                   Get | <small class="na"/>        | [get][Compable.get]             |
 * |                   Set | [set][Compable.set]        |                                 |
 * |                 Clear |                            | [+=][Compable.plusAssign]       |
 * |                Delete | [-=][Compable.minusAssign] |                                 |
 * |                Toggle | [toggle][Compable.toggle]  | <small class="na"/>            |
 *
 * | [Possession] extensions | Terminal, non-Returning     | Terminal, Returning   | non-Terminal, non-Returning     | non-Terminal, Returning |
 * |------------------------:|-----------------------------|-----------------------|---------------------------------|-------------------------|
 * |                     Get | <small class="na"/>         | <small class="na"/>   | <small class="na"/>             | [get][Possession.get]   |
 * |                     Set | [assign][Possession.assign] |                       | [assignIfNotPresent][Possession.assignIfNotPresent] |     |
 * |                   Clear | [assignDefault][Possession.assignDefault] |         | [materialize][Possession.materialize] |                   |
 * |                  Delete | [delete][Possession.delete] |                       | [unset][Possession.unset]       |                         |
 * |                  Toggle | [toggle][Possession.toggle] | <small class="na"/>   | <small class="na"/>             | <small class="na"/>     |
 *
 * @since 0.0.1-alpha.4
 */
public interface MutableEcs : Ecs {
    /**
     * Adds [value] as a component of the provided type onto this [entity],
     * overriding any values that may have been set prior.
     *
     * This is a **terminal** setter method;
     * Multiple concurrent invocations are considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see getAndSet
     * @see setIfNotPresent
     * @see setToDefault
     * @see remove
     */
    public operator fun <T> set(entity: Compable, componentType: Entity<T>, value: T)

    /**
     * Adds [value] as a component of the provided type onto this [entity],
     * returning the previous value if any (and [componentType].[default][Entity.default] otherwise).
     *
     * This is a **terminal** setter method;
     * Multiple concurrent invocations are considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see set
     * @see getOrSet
     * @see getAndSetToDefault
     * @see getAndRemove
     */
    public fun <T> getAndSet(entity: Compable, componentType: Entity<T>, value: T): T

    /**
     * If this [entity] does not have a component of the provided type, adds [value] as such.
     *
     * This is a **non-terminal** setter method;
     * The implementation is free to choose any value given multiple concurrent invocations.
     *
     * @since 0.0.1-alpha.4
     * @see getOrSet
     * @see set
     * @see materialize
     * @see unset
     */
    public fun <T> setIfNotPresent(entity: Compable, componentType: Entity<T>, value: T)

    /**
     * Retrieves the component of the provided type from this [entity], or it does not have one, adds [value] as such.
     *
     * This is a **non-terminal** setter method;
     * The implementation is free to choose any value given multiple concurrent invocations,
     * but the provided one will always be returned.
     *
     * @since 0.0.1-alpha.4
     * @see setIfNotPresent
     * @see getAndSet
     * @see getAndMaterialize
     * @see getAndUnset
     */
    public fun <T> getOrSet(entity: Compable, componentType: Entity<T>, value: T): T

    /**
     * Adds the [default][Entity.default] component of the provided type onto this [entity],
     * overriding any values that might have been set prior.
     *
     * This is a **terminal** setter method;
     * Multiple concurrent invocations are considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see getAndSetToDefault
     * @see materialize
     * @see set
     * @see remove
     */
    public fun <T> setToDefault(entity: Compable, componentType: Entity<T>)

    /**
     * Adds the [default][Entity.default] component of the provided type onto this [entity],
     * returning the previous value if there was any.
     *
     * This is a **terminal** setter method;
     * Multiple concurrent invocations are considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see setToDefault
     * @see getAndMaterialize
     * @see getAndSet
     * @see getAndRemove
     */
    public fun <T> getAndSetToDefault(entity: Compable, componentType: Entity<T>): T

    /**
     * If this [entity] does not have a component of the provided type, adds the [default][Entity.default] as such.
     *
     * This is a **non-terminal** setter method;
     * The implementation is free to choose any value given multiple concurrent invocations.
     *
     * @since 0.0.1-alpha.4
     * @see getAndMaterialize
     * @see setToDefault
     * @see setIfNotPresent
     * @see unset
     */
    public fun <T> materialize(entity: Compable, componentType: Entity<T>)

    /**
     * If this [entity] does not have a component of the provided type, adds the [default][Entity.default] as such,
     * returning its value.
     *
     * This is a **non-terminal** setter method;
     * The implementation is free to choose any value given multiple concurrent invocations,
     * but the provided one will always be returned.
     *
     * @since 0.0.1-alpha.4
     * @see materialize
     * @see getAndSetToDefault
     * @see getOrSet
     * @see getAndUnset
     */
    public fun <T> getAndMaterialize(entity: Compable, componentType: Entity<T>): T

    /**
     * If this [entity] already has a component of the provided type, [remove][MutableEcs.remove]s it;
     * Else, adds a default one.
     *
     * This is a **terminal** setter, as toggling is an inherently thread-unsafe operation;
     * Multiple concurrent invocations are considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see setToDefault
     */
    public fun toggle(entity: Compable, componentType: Entity<*>)

    /**
     * Removes the component of the provided type from this [entity], if any.
     *
     * This operation will also remove all components added to the [Possession], and cannot be reversed.
     *
     * This is a **terminal** setter method; Unlike [unset],
     * Unlike [unset], invocation of other setter methods (terminal or not) is considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see getAndRemove
     * @see unset
     * @see setToDefault
     * @see set
     */
    public fun remove(entity: Compable, componentType: Entity<*>)

    /**
     * Removes the component of the provided type from this [entity] and returns it,
     * or [default][Entity.default] if there wasn't one.
     *
     * This operation will also remove all components added to the [Possession], and cannot be reversed.
     *
     * This is a **terminal** setter method;
     * Unlike [getAndUnset], invocation of other setter methods (terminal or not) is considered undefined behaviour.
     *
     * @since 0.0.1-alpha.4
     * @see remove
     * @see getAndUnset
     * @see getAndSetToDefault
     * @see getAndSet
     */
    public fun <T> getAndRemove(entity: Compable, componentType: Entity<T>): T

    /**
     * Removes the component of the provided type from this [entity], if any.
     *
     * This operation will also remove all components added to the [Possession], and cannot be reversed.
     *
     * This is a **non-terminal** setter method; Unlike [remove],
     * it only guarantees the current value of the component will be forgotten.
     *
     * @since 0.0.1-alpha.4
     * @see getAndUnset
     * @see remove
     * @see setToDefault
     * @see set
     */
    public fun unset(entity: Compable, componentType: Entity<*>)

    /**
     * Removes the component of the provided type from this [entity], if any, and returns it.
     *
     * This operation will also remove all components added to the [Possession], and cannot be reversed.
     *
     * This is a **non-terminal** setter method; Unlike [remove],
     * it only guarantees the current value of the component will be forgotten.
     *
     * @since 0.0.1-alpha.4
     * @see getAndUnset
     * @see remove
     * @see setToDefault
     * @see set
     */
    public fun <T> getAndUnset(entity: Compable, componentType: Entity<T>): T

    /**
     * Iterates over every entity with a component of the provided type,
     * and applies the provided transformation to it.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T> mutate(componentType: Entity<T>, forEach: context(Ecs) Compable.(T) -> T)

    /**
     * Iterates over every entity with a component of all provided types,
     * and applies the provided transformation to the first.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T> mutate(
        mutatedComponentType: Entity<T>,
        vararg componentTypes: Entity<*>,
        onlyMutateSetComponents: Boolean = mutatedComponentType.onlyMutateSetComponentsByDefault,
        forEach: context(Ecs) Compable.(T) -> T
    )

    /**
     * Iterates over every entity with a component of both provided types,
     * and applies the provided transformation to the first.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T, T2> mutate(
        mutatedComponentType: Entity<T>,
        componentType2: Entity<T2>,
        onlyMutateSetComponents: Boolean = mutatedComponentType.onlyMutateSetComponentsByDefault,
        forEach: context(Ecs) Compable.(T, T2) -> T
    )

    /**
     * Iterates over every entity with a component of all provided types,
     * and applies the provided transformation to the first.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T, T2, T3> mutate(
        mutatedComponentType: Entity<T>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        onlyMutateSetComponents: Boolean = mutatedComponentType.onlyMutateSetComponentsByDefault,
        forEach: context(Ecs) Compable.(T, T2, T3) -> T
    )

    /**
     * Iterates over every entity with a component of all provided types,
     * and applies the provided transformation to the first.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T, T2, T3, T4> mutate(
        mutatedComponentType: Entity<T>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        onlyMutateSetComponents: Boolean = mutatedComponentType.onlyMutateSetComponentsByDefault,
        forEach: context(Ecs) Compable.(T, T2, T3, T4) -> T
    )

    /**
     * Iterates over every entity with a component of all provided types,
     * and applies the provided transformation to the first.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T, T2, T3, T4> mutate(
        mutatedComponentType: Entity<T>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        vararg componentTypes: Entity<*>,
        onlyMutateSetComponents: Boolean = mutatedComponentType.onlyMutateSetComponentsByDefault,
        forEach: context(Ecs) Compable.(T, T2, T3, T4) -> T
    )

    /**
     * Iterates over every entity with a component of all provided component types,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <R> set(
        modifiedComponentType: Entity<R>,
        vararg componentTypes: Entity<*>,
        forEach: context(Ecs) Compable.() -> R
    )

    /**
     * Iterates over every entity with a component of the provided type,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T, R> set(
        modifiedComponentType: Entity<R>,
        componentType: Entity<T>,
        forEach: context(Ecs) Compable.(T) -> R
    )

    /**
     * Iterates over every entity with a component of both provided component types,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, R> set(
        modifiedComponentType: Entity<R>,
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        forEach: context(Ecs) Compable.(T1, T2) -> R
    )

    /**
     * Iterates over every entity with a component of all provided component types,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3, R> set(
        modifiedComponentType: Entity<R>,
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        forEach: context(Ecs) Compable.(T1, T2, T3) -> R
    )

    /**
     * Iterates over every entity with a component of all provided component types,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3, T4, R> set(
        modifiedComponentType: Entity<R>,
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        forEach: context(Ecs) Compable.(T1, T2, T3, T4) -> R
    )

    /**
     * Iterates over every entity with a component of all provided component types,
     * and sets the result onto each.
     *
     * @since 0.0.1-alpha.4
     */
    public fun <T1, T2, T3, T4, R> set(
        modifiedComponentType: Entity<R>,
        componentType1: Entity<T1>,
        componentType2: Entity<T2>,
        componentType3: Entity<T3>,
        componentType4: Entity<T4>,
        vararg componentTypes: Entity<*>,
        forEach: context(Ecs) Compable.(T1, T2, T3, T4) -> R
    )

    /**
     * Adds this [NamedEntity] onto the ECS.
     *
     * This is a **non-terminal** setter.
     *
     * @since 0.0.1-alpha.4
     * @see contains
     * @see delete
     */
    public fun add(entity: NamedEntity<*>)

    /**
     * Removes this [campable] and all its components.
     *
     * This operation will also remove all components added to the [Possession], and cannot be reversed.
     *
     * If [campable] is an [Entity], all components of its associated type will be removed as well.
     * If it is a [Possession], the described component will be removed if it exists.
     *
     * @since 0.0.1-alpha.4
     * @see materialize
     * @see contains
     */
    public fun delete(campable: Compable)

    /**
     * @since 0.0.1-alpha.4
     * @see getDefaultOf
     * @see getAndSetDefaultOf
     * @see resetComponentTypeDefaults
     */
    public fun <T> setDefaultOf(componentType: Entity<T>, value: T)

    /**
     * Sets the [default][Entity.default] value of this [componentType], returning the old one.
     *
     * @since 0.0.1-alpha.4
     *
     * @see getDefaultOf
     * @see setDefaultOf
     * @see resetComponentTypeDefaults
     */
    public fun <T> getAndSetDefaultOf(componentType: Entity<T>, value: T): T

    /**
     * @since 0.0.1-alpha.4
     * @see getDefaultOf
     * @see setDefaultOf
     * @see getAndSetDefaultOf
     */
    public fun resetComponentTypeDefaults()
}
