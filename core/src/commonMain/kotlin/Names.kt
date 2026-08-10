package quest.laxla.spock.core

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import quest.laxla.spock.core.Name.Companion.DOT
import quest.laxla.spock.core.Name.Companion.Root
import quest.laxla.spock.core.Name.Companion.SLASH

private fun Name(
	parts: ImmutableList<Name.Part>,
	isAbsolute: Boolean,
	slashed: String?,
	dotted: String?
): Name = when (isAbsolute) {
	true -> AbsoluteName.ofNoCheck(parts, slashString = slashed, dotString = dotted)
	else -> RelativeName.ofNoCheck(parts, slashString = slashed, dotString = dotted)
}

/**
 * Constructs a [Name] with the given [parts].
 *
 * @since 0.0.1-alpha.4
 */
public fun Name(parts: ImmutableList<Name.Part>, isAbsolute: Boolean): Name = Name(parts, isAbsolute, null, null)

/**
 * @param separator [SLASH] or [DOT] to only allow parsing slash- or dot-formatted names, respectively, or `null` to allow either.
 * @throws IllegalArgumentException if parsing failed.
 * @author Laxystem
 */
private fun Name(name: String, separator: Char? = null): Name {
	require(name.isNotEmpty()) { "Name mustn't be empty" }

	var separator: Char? = separator
	var startsWithSeparator = false

	val builder = StringBuilder()
	val parts = persistentListOf<Name.Part>().builder()

	val firstChar = name[0]
	when (firstChar) {
		separator -> {
			startsWithSeparator = true
		}

		in Name.SEPARATORS if separator == null -> {
			separator = firstChar
			startsWithSeparator = true
		}

		else -> builder.append(firstChar)
	}

	fun addPart() {
		val partString = builder.toString()
		val part = try {
			Name.Part(partString)
		} catch (e: IllegalArgumentException) {
			throw IllegalArgumentException(
				"Illegal name part [$parts] in name [$name] at index [${parts.size}]",
				e
			)
		}

		parts.add(part)
		builder.clear()
	}

	fun build(): Name {
		addPart()

		var slashed: String? = null
		var dotted: String? = null

		return Name(
			isAbsolute = when (separator) {
				SLASH -> {
					slashed = name
					startsWithSeparator
				}

				DOT -> {
					dotted = name
					!startsWithSeparator
				}

				else -> throw IllegalArgumentException("Cannot infer whether '$firstChar' is an absolute or relative Name.")
			},
			parts = parts.build(),
			slashed = slashed,
			dotted = dotted
		)
	}

	if (name.length == 1) return when (separator) {
		SLASH if startsWithSeparator -> Root
		DOT if startsWithSeparator -> NamespaceRoot
		else -> build()
	}

	for (index in 1..name.lastIndex) when (val char = name[index]) {
		separator -> addPart()

		in Name.SEPARATORS if separator == null -> {
			separator = char
			addPart()
		}

		else -> builder.append(char)
	}

	return build()
}

/**
 * Converts this [String] to a [Name].
 * Supports both dots and slashes as a separator.
 *
 * @throws IllegalArgumentException on failure.
 * @since 0.0.1-alpha.4
 */
public fun String.toNameOrThrow(): Name = Name(name = this)

/**
 * Converts this [String] to a [Name].
 * Supports both dots and slashes as a separator.
 *
 * @return `null` on failure.
 * @since 0.0.1-alpha.4
 */
public fun String.toNameOrNull(): Name? = try {
	toNameOrThrow()
} catch (_: IllegalArgumentException) {
	null
}

/**
 * Localizes the provided [RelativeName] into the [namespace] in context.
 *
 * @since 0.0.1-alpha.4
 */
context(namespace: NamespaceContext)
public fun RelativeName.inNamespace(): AbsoluteName = namespace.get()[this]

/**
 * If this [Name] is an [AbsoluteName], returns it, and if it is a [RelativeName], localizes it.
 *
 * @since 0.0.1-alpha.4
 */
context(namespace: NamespaceContext)
public fun Name.inNamespaceIfRelative(): AbsoluteName = when (this) {
	is AbsoluteName -> this
	is RelativeName -> inNamespace()
}

/**
 * Converts this [String] to an [AbsoluteName]. Supports both dots and slashes as separators.
 *
 * @throws IllegalArgumentException if an invalid [Name] or a [RelativeName].
 * @since 0.0.1-alpha.4
 */
public fun AbsoluteName(name: String): AbsoluteName {
	val name = Name(name)
	return name as? AbsoluteName ?: throw IllegalArgumentException("Expected absolute name, got relative [$name]")
}

/**
 * Converts this [String] to a [RelativeName]. Supports both dots and slashes as separators.
 *
 * @throws IllegalArgumentException if an invalid [Name] or an [AbsoluteName].
 * @since 0.0.1-alpha.4
 */
public fun RelativeName(name: String): RelativeName {
	val name = Name(name)
	return name as? RelativeName ?: throw IllegalArgumentException("Expected relative name, got absolute [$name]")
}

/**
 * @since 0.0.1-alpha.4
 */
public fun Name.toSlashFormattedStringOrDot(): String = toSlashFormattedString() ?: DOT.toString()

/**
 * @since 0.0.1-alpha.4
 */
public fun Name.toDotFormattedStringOrSlash(): String = toDotFormattedString() ?: SLASH.toString()
