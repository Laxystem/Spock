package quest.laxla.spock.core

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.asSerializationException
import quest.laxla.spock.assert
import quest.laxla.spock.core.Name.Companion.DOT
import quest.laxla.spock.core.Name.Companion.SLASH
import quest.laxla.spock.removingLast
import quest.laxla.spock.skipFirst
import kotlin.jvm.JvmSynthetic

/**
 * Properly-[Namespace]d, absolute [Name].
 *
 * @since 0.0.1-alpha.4
 */
@Serializable(AbsoluteName.Companion::class)
@FutureTranslation(hebrew = "שם מלא")
public class AbsoluteName private constructor(
	parts: ImmutableList<Name.Part>?,
	slashString: String?,
	dotString: String?
) : Name {
	init {
		assert(parts != null || dotString != null || slashString != null) { "Cannot create out of nothing." }
		assert(slashString == null || slashString[0] == '/') { "Slashed name format must be absolute." }
		assert(dotString == null || dotString[0] != '.') { "Dotted name format must be absolute." }
	}

	private val _parts = atomic(parts?.toPersistentList())
	private val _dotString = atomic(dotString)
	private val _slashString = atomic(slashString)

	/**
	 * @since 0.0.1-alpha.4
	 */
	public constructor(parts: ImmutableList<Name.Part>) : this(parts, slashString = null, dotString = null)

	private fun dottedSegments() = _dotString.value
		?.splitToSequence('.')


	private fun slashedSegments() = _slashString.value
		?.splitToSequence('/')
		?.skipFirst()

	override val parts: PersistentList<Name.Part>
		get() = _parts.updateAndGet {
			it ?: (dottedSegments() ?: slashedSegments())
				?.map(Name::Part)
				?.toPersistentList() ?: error("Should always have parts, slashed string, or dotted string")
		}!!

	override val parent: AbsoluteName?
		get() = if (parts.isEmpty()) null else AbsoluteName(
			parts.removingLast(),
			slashString = _slashString.value?.substringBeforeLast('/')?.ifEmpty { "/" },
			dotString = _dotString.value?.substringBeforeLast('.')?.ifEmpty { null }
		)

	override fun child(part: Name.Part): AbsoluteName = AbsoluteName(
		_parts.value?.adding(part),
		_slashString.value?.plus(SLASH)?.plus(part.toString()),
		_dotString.value?.plus(DOT)?.plus(part.toString())
	)

	override fun child(parts: Collection<Name.Part>): AbsoluteName = if (parts.isEmpty()) this else AbsoluteName(
		_parts.value?.addingAll(parts),
		_slashString.value?.plus(parts.joinToString(SLASH.toString(), SLASH.toString())),
		_dotString.value?.plus(parts.joinToString(DOT.toString(), DOT.toString()))
	)

	override fun toSlashFormattedString(): String = _slashString.updateAndGet {
		it ?: parts.joinToString(separator = "/", prefix = "/")
	}!!

	override fun toDotFormattedString(): String? = _dotString.updateAndGet {
		it ?: if (parts.isEmpty()) null else parts.joinToString(separator = ".")
	}

	override fun toSlashedStringOrNull(): String = toSlashFormattedString()

	override fun toDottedStringOrNull(): String? = if (parts.size > 1) toDotFormattedString() else null

	override fun toString(): String = toSlashFormattedString()

	override fun equals(other: Any?): Boolean = other is AbsoluteName && toString() == other.toString()

	override fun hashCode(): Int = toString().hashCode()

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : KSerializer<AbsoluteName> {
		override val descriptor: SerialDescriptor
			get() = PrimitiveSerialDescriptor(
				serialName = "quest.laxla.spock.AbsoluteName",
				kind = PrimitiveKind.STRING
			)

		override fun serialize(encoder: Encoder, value: AbsoluteName) {
			encoder.encodeString(value.toString())
		}

		override fun deserialize(decoder: Decoder): AbsoluteName = try {
			AbsoluteName(decoder.decodeString())
		} catch (e: IllegalArgumentException) {
			throw e.asSerializationException()
		}

		@JvmSynthetic
		internal fun ofNoCheck(
			parts: ImmutableList<Name.Part>,
			slashString: String?,
			dotString: String?
		) = AbsoluteName(parts, slashString, dotString)
	}
}