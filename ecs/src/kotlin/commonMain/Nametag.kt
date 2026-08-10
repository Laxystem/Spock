package quest.laxla.spock.ecs

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

/**
 * An [ComponentType.Tag] assigned a [Name].
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
@Serializable
public value class Nametag(override val path: Name) : NamedEntity<Unit> {
	override val dataType: KClass<Unit> get() = Unit::class
	override val defaultDefault: Unit get() = Unit

	override fun toString(): String = "Tag($path)"
}
