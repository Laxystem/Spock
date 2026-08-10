package quest.laxla.spock.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * Represents metadata associated with a [RawResource], usually within a `.properties` file alongside the main resource.
 *
 * @property default the fallback value returned when the property is missing.
 * @property warnMissing whether this property is required, and a message should be logged if it is missing.
 * @since 0.0.1-alpha.4
 */
public data class Property<T>(
	public val name: AbsoluteName,
	public val default: T,
	public val serializer: KSerializer<T>,
	public val warnMissing: Boolean = true
) {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : NamespaceContext by Namespace.Engine {
		/**
		 * Resource version.
		 *
		 *
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val version: Property<ResourceVersion?> = Property(name = ".version", default = null)
	}
}


/**
 * Creates a [Property].
 *
 * @param warnMissing whether this property is required, and a message should be logged if it is missing.
 * @since 0.0.1-alpha.4
 */
context(namespace: NamespaceContext)
public fun <T> Property(
	name: String,
	default: T,
	serializer: KSerializer<T>,
	warnMissing: Boolean = true
): Property<T> = Property(name.toNameOrThrow().inNamespaceIfRelative(), default, serializer, warnMissing)

/**
 * Creates a [Property], with its [KSerializer] inferred.
 *
 * @param warnMissing whether this property is required, and a message should be logged if it is missing.
 * @see SerializersModule.serializer
 * @since 0.0.1-alpha.4
 */
context(namespace: NamespaceContext)
public inline fun <reified T> Property(
	name: String,
	default: T,
	serializersModule: SerializersModule = EmptySerializersModule(),
	warnMissing: Boolean = true
): Property<T> = Property(
	name,
	default,
	serializersModule.serializer(),
	warnMissing
)

/**
 * Creates a [Property] property with a default value of `null`, with its [KSerializer] inferred.
 *
 * @param warnMissing whether this property is required, and a message should be logged if it is missing.
 * @see SerializersModule.serializer
 * @since 0.0.1-alpha.4
 */
context(namespace: NamespaceContext)
public inline fun <reified T> Property(
	name: String,
	default: Nothing?,
	serializersModule: SerializersModule = EmptySerializersModule(),
	warnMissing: Boolean = true
): Property<T?> = Property(
	name,
	default,
	serializersModule.serializer(),
	warnMissing
)
