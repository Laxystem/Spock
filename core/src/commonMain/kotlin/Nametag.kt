package quest.laxla.spock

import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * An [Entity.Tag] assigned a [Name].
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
public value class Nametag(override val name: Name) : NamedEntity<Unit> {
    override val dataType: KClass<Unit> get() = Unit::class
    override val default: Unit get() = Unit
}
