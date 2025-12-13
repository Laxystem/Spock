package quest.laxla.spock

import kotlin.jvm.JvmInline
import kotlin.reflect.KClass


/**
 * Member of the Entity-Component-System triad. Optionally carries data.
 *
 * @since 0.0.1-alpha.4
 */
public sealed interface Entity<T> : Compable {
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
    public val default: T

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
    public typealias Tag = Entity<Unit>

    /**
     * Nameless, dataless [Entity].
     *
     * @property id The numeral identifier of this [Entity].
     * @since 0.0.1-alpha.4
     */
    @JvmInline
    public value class Autonomous(public val id: UInt) : Tag {
        override val dataType: KClass<Unit> get() = Unit::class
        override val default: Unit get() = Unit
    }
}
