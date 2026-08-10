package quest.laxla.spock.registry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.core.AbsoluteName

/**
 * References a resource stored in a [Registry] without actually holding it.
 *
 * @param T the type of the specific resource referenced.
 * @since 0.0.1-alpha.4
 */
@Serializable(Key.Companion::class)
public data class Key<out T : E, E : Any>(
	public override val classifier: ResourceClassifier<E>,
	public override val name: AbsoluteName
) : Referant<T, E> {
	@Deprecated(
		message = "Unnecessary call",
		level = DeprecationLevel.HIDDEN,
		replaceWith = ReplaceWith(expression = "this")
	)
	override val key: Key<T, E>
		get() = this

	@Deprecated(
		message = "Unnecessary call",
		level = DeprecationLevel.HIDDEN,
		replaceWith = ReplaceWith(expression = "this")
	)
	override fun toKeyOrNull(): Key<T, E> = this

	@Deprecated(
		message = "Unnecessary call",
		level = DeprecationLevel.HIDDEN,
		replaceWith = ReplaceWith(expression = "this")
	)
	override fun toKeyOrThrow(): Key<T, E> = this

	override fun toString(): String = toKeylikeString()

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : KSerializer<Key<*, *>> {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public const val SEPARATOR: String = ":"

		override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
			serialName = "quest.laxla.spock.registry.Key",
			kind = PrimitiveKind.STRING
		)

		override fun serialize(encoder: Encoder, value: Key<*, *>) {
			encoder.encodeString(value.toString())
		}

		override fun deserialize(decoder: Decoder): Key<*, *> {
			val string = decoder.decodeString()

			return Key<Any, Any>(
				classifier = ResourceType reference AbsoluteName(string.substringBefore(SEPARATOR)),
				name = AbsoluteName(string.substringAfter(SEPARATOR))
			)
		}
	}
}

/**
 * Refers to an entry within a [Registry].
 *
 * Equivalent to a Minecraft `ResourceKey`.
 *
 * @since 0.0.1-alpha.4
 * @see Resource
 * @see Key
 */
public typealias ResourceKey<E> = Key<E, E>
