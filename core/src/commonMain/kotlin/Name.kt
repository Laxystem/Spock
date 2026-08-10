package quest.laxla.spock.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.asSerializationException
import kotlin.jvm.JvmInline

/**
 * Globally-unique Resource name composed of alphanumeric `snake_case` [Part]s separated by slashes or dots.
 *
 * Names can be formatted either with dots or with slashes, and may be [RelativeName] or [AbsoluteName];
 * In the dotted format, a separator is prepended for relative paths,
 * and in the slashed format, for absolute paths.
 *
 * Examples of [Name]s are:
 * * `/` - the [Root] name. Cannot be represented in dot format.
 * * `.` - the [RelativeName.Root] name, i.e. [Namespace] the path is relative to. Cannot be represented in slash format.
 * * `/engine` - [Namespace.Engine], the namespace of the Spock engine itself.
 * * `foo/bar`, `.foo.bar` - these are equivalent, both a relative path requiring a [Namespace] to be made absolute.
 * * `/foo/bar`, `foo.bar` - again equivalent, these represent a [Namespace] or an absolute path.
 *
 * Calling [toString] on this object will always return the *prefixed* version for brevity.
 * Consider using [toDotFormattedStringOrSlash] or [toSlashFormattedStringOrDot] if you require consistent formatting.
 *
 * @since 0.0.1-alpha.4
 */
@Serializable(Name.Companion::class)
@FutureTranslation(hebrew = "שם")
public sealed interface Name {
	/**
	 * An identical [Name] save for missing the last element.
	 *
	 * @return `null` if this the [Root] or a [NamespaceRoot].
	 * @since 0.0.1-alpha.4
	 */
	public val parent: Name?

	/**
	 * @since 0.0.1-alpha.4
	 */
	public val parts: PersistentList<Part>

	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun child(part: Part): Name

	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun child(parts: Collection<Part>): Name

	/**
	 * Formats this [Name] as a dot-separated [String].
	 *
	 * @return `null` if the resulting name would not include a dot.
	 * @since 0.0.1-alpha.4
	 * @see toDotFormattedString
	 */
	public fun toDottedStringOrNull(): String?

	/**
	 * Formats this [Name] as a slash-separated [String].
	 *
	 * @return `null` if the resulting name would not include a slash.
	 * @since 0.0.1-alpha.4
	 * @see toSlashFormattedString
	 */
	public fun toSlashedStringOrNull(): String?

	/**
	 * Formats this [Name] as a dot-separated [String], even if it would be impossible to recognize it as such.
	 *
	 * @return `null` if this is the [Root] name.
	 * @since 0.0.1-alpha.4
	 */
	public fun toDotFormattedString(): String?

	/**
	 * Formats this [Name] as a dot-separated [String], even if it would be impossible to recognize it as such.
	 *
	 * @return `null` if this is the [NamespaceRoot].
	 * @since 0.0.1-alpha.4
	 */
	public fun toSlashFormattedString(): String?

	/**
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	public value class Part(private val string: String) {
		init {
			require(string.isNotEmpty()) { "Name part mustn't be empty" }
			for ((index, char) in string.withIndex()) require(char in 'a'..'z' || char in '0'..'9' || char == '_') { "Name part [$string] includes illegal character '$char' at index $index" }
		}

		override fun toString(): String = string
	}

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : KSerializer<Name> {
		/**
		 * Separator used in slash-formatted [Name]s.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public const val SLASH: Char = '/'

		/**
		 * Separator used in dot-formatted [Name]s.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public const val DOT: Char = '.'

		/**
		 * String containing all [Char]s that may act as [Part] separators.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public const val SEPARATORS: String = "$SLASH$DOT"

		/**
		 * The root absolute name.

		 * @since 0.0.1-alpha.4
		 */
		public val Root: AbsoluteName = AbsoluteName(persistentListOf())

		/**
		 * The formatting used for unknown names.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val UnknownFormatting: String = "<<${Namespace.Unknown.name.toDotFormattedString()}>>"

		override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
			serialName = "quest.laxla.spock.Name",
			kind = PrimitiveKind.STRING
		)

		override fun serialize(encoder: Encoder, value: Name) {
			encoder.encodeString(value.toString())
		}

		override fun deserialize(decoder: Decoder): Name = try {
			decoder.decodeString().toNameOrThrow()
		} catch (e: IllegalArgumentException) {
			throw e.asSerializationException()
		}
	}
}

