package quest.laxla.spock.io

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.Source
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import quest.laxla.spock.emptyString
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val logger = KotlinLogging.logger { }

/**
 * Transforms a [Serializable] class' properties into a single flat [Map] consisting of
 * string keys and primitive type values prefixed with some arbitrary [String], and vice versa.
 *
 * @param unprefixed the regular, built-in KotlinX Serialization string map [SerialFormat] to be delegated to.
 * @param prefixSeparator the string between the prefix and the rest of the property name.
 *
 * @see Properties
 */
@ExperimentalSerializationApi
public sealed class PrefixedProperties(
	public val unprefixed: Properties,
	public val prefixSeparator: String
) : SerialFormat {
	override val serializersModule: SerializersModule
		get() = unprefixed.serializersModule

	private open inner class FilteredMap(
		private val base: Map<String, String>,
		val prefix: String
	) : Map<String, String> {
		private fun fulfillsPrefix(string: String) = string == prefix || string.startsWith(prefix + prefixSeparator)
		private fun String.prependPrefix() = if (isEmpty()) prefix else prefix + prefixSeparator + this

		override val size: Int
			get() = base.keys.count(::fulfillsPrefix)

		private inner class KeyIterator : AbstractIterator<String>() {
			private val baseIterator = base.keys.iterator()

			override fun computeNext() {
				for (key in baseIterator) if (fulfillsPrefix(key)) {
					val fullPrefixLength = prefix.length + prefixSeparator.length
					setNext(if (key.length > fullPrefixLength) key.substring(fullPrefixLength) else emptyString())
					return
				}

				done()
			}
		}

		private inner class Keys : Set<String> {
			override val size: Int
				get() = this@FilteredMap.size

			override fun isEmpty(): Boolean = !iterator().hasNext()

			override fun contains(element: String): Boolean = this@FilteredMap.containsKey(element)

			override fun iterator(): Iterator<String> = KeyIterator()

			override fun containsAll(elements: Collection<String>): Boolean {
				for (element in elements) if (element !in this) return false

				return true
			}
		}

		override val keys: Set<String> = Keys()

		override fun isEmpty(): Boolean = keys.isEmpty()

		override fun containsKey(key: String): Boolean = base.containsKey(key.prependPrefix())

		override fun get(key: String): String? = base[key.prependPrefix()]
		val primitiveValue: String get() = base.getValue(prefix)

		private fun unoptimized(function: String) = logger.warn {
			"PrefixedProperties.FilteredMap.$function was called despite being previously unused by KotlinX Serialization, and should be optimized"
		}

		private val filteredMap get() = base.filterKeys(::fulfillsPrefix)

		override val values: Collection<String>
			get() {
				unoptimized("values")
				return filteredMap.values
			}

		override val entries: Set<Map.Entry<String, String>>
			get() {
				unoptimized("entries")
				return filteredMap.entries
			}

		override fun containsValue(value: String): Boolean {
			unoptimized("containsValue")
			return filteredMap.containsValue(value)
		}
	}

	private class NoOpCodec(
		override val descriptor: SerialDescriptor,
		dummy: Nothing? = null,
		private val decoder: (Decoder) -> Unit = {},
		private val encoder: (Encoder) -> Unit = {}
	) : KSerializer<Unit> {
		override fun deserialize(decoder: Decoder) = decoder(decoder)
		override fun serialize(encoder: Encoder, value: Unit) = encoder(encoder)
	}

	private fun decoder(descriptor: SerialDescriptor, map: Map<String, String>): Decoder {
		lateinit var result: Decoder
		unprefixed.decodeFromStringMap(NoOpCodec(descriptor, decoder = { result = it }), map)
		return result
	}

	// we just need to pass a type-check, it is not actually used.
	private inner class NoOpCompositeDecoder : CompositeDecoder {
		override val serializersModule: SerializersModule
			get() = this@PrefixedProperties.serializersModule

		override fun endStructure(descriptor: SerialDescriptor) = throw NotImplementedError()

		override fun decodeElementIndex(descriptor: SerialDescriptor): Int = throw NotImplementedError()

		override fun decodeBooleanElement(
			descriptor: SerialDescriptor,
			index: Int
		): Boolean = throw NotImplementedError()

		override fun decodeByteElement(
			descriptor: SerialDescriptor,
			index: Int
		): Byte = throw NotImplementedError()

		override fun decodeCharElement(
			descriptor: SerialDescriptor,
			index: Int
		): Char = throw NotImplementedError()

		override fun decodeShortElement(
			descriptor: SerialDescriptor,
			index: Int
		): Short = throw NotImplementedError()

		override fun decodeIntElement(
			descriptor: SerialDescriptor,
			index: Int
		): Int = throw NotImplementedError()

		override fun decodeLongElement(
			descriptor: SerialDescriptor,
			index: Int
		): Long = throw NotImplementedError()

		override fun decodeFloatElement(
			descriptor: SerialDescriptor,
			index: Int
		): Float = throw NotImplementedError()

		override fun decodeDoubleElement(
			descriptor: SerialDescriptor,
			index: Int
		): Double = throw NotImplementedError()

		override fun decodeStringElement(
			descriptor: SerialDescriptor,
			index: Int
		): String = throw NotImplementedError()

		override fun decodeInlineElement(
			descriptor: SerialDescriptor,
			index: Int
		): Decoder = throw NotImplementedError()

		override fun <T> decodeSerializableElement(
			descriptor: SerialDescriptor,
			index: Int,
			deserializer: DeserializationStrategy<T>,
			previousValue: T?
		): T = throw NotImplementedError()

		@ExperimentalSerializationApi
		override fun <T : Any> decodeNullableSerializableElement(
			descriptor: SerialDescriptor,
			index: Int,
			deserializer: DeserializationStrategy<T?>,
			previousValue: T?
		): T? = throw NotImplementedError()
	}

	private inner class DecoderWrapper(private val map: FilteredMap) : Decoder {
		override val serializersModule: SerializersModule
			get() = this@PrefixedProperties.serializersModule

		private val dummyCompositeDecoder by lazy(::NoOpCompositeDecoder)

		@OptIn(InternalSerializationApi::class)
		override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
			if (deserializer is AbstractPolymorphicSerializer<*>) {
				val actualDeserializer = deserializer.findPolymorphicSerializer(dummyCompositeDecoder, map["type"])

				@Suppress("UNCHECKED_CAST")
				actualDeserializer.deserialize(this) as T
			} else deserializer.deserialize(this)

		@OptIn(ExperimentalContracts::class)
		private inline fun <T> correctExceptions(block: () -> T): T {
			contract {
				callsInPlace(block, InvocationKind.AT_MOST_ONCE)
			}

			return try {
				block()
			} catch (e: IllegalArgumentException) {
				throw SerializationException(e)
			} catch (e: NoSuchElementException) {
				throw SerializationException(e)
			}
		}

		override fun decodeNotNullMark(): Boolean = map.isNotEmpty()
		override fun decodeNull(): Nothing? = null
		override fun decodeBoolean(): Boolean = correctExceptions { map.primitiveValue.toBoolean() }
		override fun decodeByte(): Byte = correctExceptions { map.primitiveValue.toByte() }
		override fun decodeShort(): Short = correctExceptions { map.primitiveValue.toShort() }
		override fun decodeChar(): Char = correctExceptions { map.primitiveValue.single() }
		override fun decodeInt(): Int = correctExceptions { map.primitiveValue.toInt() }
		override fun decodeLong(): Long = correctExceptions { map.primitiveValue.toLong() }
		override fun decodeFloat(): Float = correctExceptions { map.primitiveValue.toFloat() }
		override fun decodeDouble(): Double = correctExceptions { map.primitiveValue.toDouble() }
		override fun decodeString(): String = map.primitiveValue

		override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
			val elementName = map.primitiveValue
			val index = enumDescriptor.getElementIndex(map.primitiveValue)

			return if (index == CompositeDecoder.UNKNOWN_NAME)
				elementName.toIntOrNull()?.takeIf { it < enumDescriptor.elementsCount }
					?: throw SerializationException("Enum '${enumDescriptor.serialName}' does not contain element with name '$elementName'")
			else index
		}

		override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

		override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
			decoder(descriptor, map).beginStructure(descriptor)
	}

	/**
	 * Decodes properties from the given [map] with a given [prefix] to a value of type [T] using the given [deserializer].
	 *
	 * [String] values are converted to respective primitive types using default conversion methods.
	 * [T] may contain properties of nullable types; they will be filled by non-null values from the [map], if present.
	 * If the [deserializer] is of a [PrimitiveKind], it'll be converted from the property named exactly as the [prefix],
	 * without a [prefixSeparator].
	 *
	 * @since 0.0.1-alpha.4
	 * @see Properties.decodeFromStringMap
	 */
	public fun <T> decodeFromStringMap(
		deserializer: DeserializationStrategy<T>,
		map: Map<String, String>,
		prefix: String
	): T = DecoderWrapper(FilteredMap(map, prefix)).decodeSerializableValue(deserializer)

	/**
	 * Decodes properties with a given [prefix] from the given Java `.properties` file in [source] form using the given [deserializer].
	 *
	 * @since 0.0.1-alpha.4
	 * @see decodeFromStringMap
	 */
	public fun <T> decodeFromSource(
		deserializer: DeserializationStrategy<T>,
		source: Source,
		prefix: String
	): T = decodeFromStringMap(deserializer, source.use { it.readProperties() }, prefix)

	public companion object Default : PrefixedProperties(Properties.Default, ".")

	override fun equals(other: Any?): Boolean = other is PrefixedProperties && unprefixed == other.unprefixed && prefixSeparator == other.prefixSeparator

	override fun hashCode(): Int {
		var result = unprefixed.hashCode()
		result = 31 * result + prefixSeparator.hashCode()
		result = 31 * result + serializersModule.hashCode()
		return result
	}
}

@ExperimentalSerializationApi
public inline fun <reified T> PrefixedProperties.decodeFromStringMap(
	map: Map<String, String>,
	prefix: String
): T = decodeFromStringMap(serializersModule.serializer(), map, prefix)

@ExperimentalSerializationApi
private class PrefixedPropertiesImpl(
	unprefixed: Properties,
	prefixSeparator: String
) : PrefixedProperties(unprefixed, prefixSeparator)

/**
 * @since 0.0.1-alpha.4
 */
@ExperimentalSerializationApi
public fun Properties.asPrefixed(separator: String = PrefixedProperties.prefixSeparator): PrefixedProperties =
	PrefixedPropertiesImpl(this, separator)
