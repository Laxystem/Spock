package quest.laxla.spock.core

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
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
 * Non-absolute [Name] relative to some [Namespace].
 *
 * @since 0.0.1-alpha.4
 */
@Serializable(RelativeName.Companion::class)
@FutureTranslation("שם חלקי")
public class RelativeName private constructor(
	parts: ImmutableList<Name.Part>?,
	dotString: String?,
	slashString: String?
) : Name {
	init {
		assert(parts != null || slashString != null || dotString != null) { "Cannot create out of nothing." }
		assert(slashString == null || slashString[0] != '/') { "Slashed name format must be relative." }
		assert(dotString == null || dotString[0] == '.') { "Dotted name format must be relative." }
	}

	private val _parts = atomic(parts?.toPersistentList())
	private val _dotString = atomic(dotString)
	private val _slashString = atomic(slashString)

	/**
	 * @since 0.0.1-alpha.4
	 */
	public constructor(parts: ImmutableList<Name.Part>) : this(parts, dotString = null, slashString = null)

	private fun dottedSegments() = _dotString.value
		?.splitToSequence('.')
		?.skipFirst()

	private fun slashedSegments() = _slashString.value
		?.splitToSequence('/')

	override val parts: PersistentList<Name.Part>
		get() = _parts.updateAndGet {
			it ?: (dottedSegments() ?: slashedSegments())
				?.map(Name::Part)
				?.toPersistentList() ?: error("Should always have parts, slashed string, or dotted string")
		}!!

	override val parent: RelativeName?
		get() = if (parts.isEmpty()) null else RelativeName(
			parts.removingLast(),
			dotString = _dotString.value?.substringBeforeLast('.')?.ifEmpty { "." },
			slashString = _slashString.value?.substringBeforeLast('/')?.ifEmpty { null }
		)

	override fun child(part: Name.Part): RelativeName = RelativeName(
		_parts.value?.adding(part),
		_slashString.value?.plus(SLASH)?.plus(part.toString()),
		_dotString.value?.plus(DOT)?.plus(part.toString())
	)

	override fun child(parts: Collection<Name.Part>): RelativeName = if (parts.isEmpty()) this else RelativeName(
		_parts.value?.addingAll(parts),
		_slashString.value?.plus(parts.joinToString(SLASH.toString(), SLASH.toString())),
		_dotString.value?.plus(parts.joinToString(DOT.toString(), DOT.toString()))
	)

	override fun toDotFormattedString(): String = _dotString.updateAndGet {
		it ?: parts.joinToString(separator = ".", prefix = ".")
	}!!

	override fun toSlashFormattedString(): String? = _slashString.updateAndGet {
		it ?: if (parts.isEmpty()) null else parts.joinToString(separator = "/")
	}

	override fun toDottedStringOrNull(): String = toDotFormattedString()

	override fun toSlashedStringOrNull(): String? = if (parts.size > 1) toSlashFormattedString() else null

	override fun toString(): String = toDotFormattedString()

	override fun equals(other: Any?): Boolean = other is RelativeName && toString() == other.toString()

	override fun hashCode(): Int = toString().hashCode()

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : KSerializer<RelativeName> {
		/**
		 * The root name for [RelativeName]s.
		 *
		 * This root is a hard border;
		 * I.e., it is impossible to access the [Name.parent] of the root name of a namespace,
		 * thus making relative names completely isolated from the global namespace and perfectly namespace-leakage-safe.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val Root: RelativeName = RelativeName(parts = persistentListOf())

		override val descriptor: SerialDescriptor
			get() = PrimitiveSerialDescriptor(
				serialName = "quest.laxla.spock.core.RelativeName",
				kind = PrimitiveKind.STRING
			)

		override fun serialize(encoder: Encoder, value: RelativeName) {
			encoder.encodeString(value.toString())
		}

		override fun deserialize(decoder: Decoder): RelativeName = try {
			RelativeName(decoder.decodeString())
		} catch (e: IllegalArgumentException) {
			throw e.asSerializationException()
		}

		@JvmSynthetic
		internal fun ofNoCheck(
			parts: ImmutableList<Name.Part>,
			dotString: String?,
			slashString: String?
		) = RelativeName(parts, dotString, slashString)
	}
}