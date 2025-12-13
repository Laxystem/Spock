package quest.laxla.spock

import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName

/**
 * Globally-unique [Resource] name composed of latin-alphabet `camelCase` elements separated by slashes, e.g. an [Entity] name.
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
public value class Name private constructor(private val name: String) {
	/**
	 * An identical [Name] save for missing the last element.
	 *
	 * @return `null` if this [Name] only has one element.
	 * @since 0.0.1-alpha.4
	 */
	public val parent: Name?
		get() = name.substringBeforeLast(SEPARATOR, missingDelimiterValue = "")
			.takeUnless(String::isEmpty)
			?.let(::Name)

	/**
	 * Creates a child [Name] with [name] appended.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public operator fun div(name: String): Name = new(this.name + SEPARATOR + name)

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public const val SEPARATOR: Char = '/'

		/**
		 * @author Laxystem
		 */
		internal fun new(name: String): Name {
			require(name.isNotBlank()) { "Resource name must not be blank." }
			require(name.all { it in 'a'..'z' || it in 'A'..'Z' || it == '/' }) { "Invalid character in resource name $name; Only latin letters and slashes are allowed." }
			// TODO: forbid consecutive slashes and capitals

			return Name(name)
		}
	}
}

/**
 * Creates a new [Name].
 *
 * @since 0.0.1-alpha.4
 */
@JvmName("from")
@Throws(IllegalArgumentException::class)
public fun String.asName(): Name = Name.new(this)

/**
 * Creates a new [Name] with [this] and [namePart] split into elements.
 *
 * @since 0.0.1-alpha.4
 */
public operator fun String.div(namePart: String): Name = asName() / namePart

/**
 * Creates a new [Name] with [this] as a prefix if it is not `null`, followed by [namePart].
 *
 * @since 0.0.1-alpha.4
 */
public operator fun Name?.div(namePart: String): Name = this?.div(namePart) ?: namePart.asName()
