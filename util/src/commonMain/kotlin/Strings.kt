package quest.laxla.spock

/**
 * Returns an empty [String], `""`.
 *
 * @since 0.0.1-alpha.4
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun emptyString(): String = ""

/**
 * Escapes the provided string to avoid markup from being accidentally applied when used in HTML files.
 *
 * This function is taken from Ktor, under the Apache License 2.0.
 *
 * @since 0.0.1-alpha.4
 */
public fun String.escapeHtml(): String {
	val text = this@escapeHtml
	if (text.isEmpty()) return text

	return buildString(length) {
		for (element in text) when (element) {
			'\'' -> append("&#x27;")
			'\"' -> append("&quot;")
			'&' -> append("&amp;")
			'<' -> append("&lt;")
			'>' -> append("&gt;")
			else -> append(element)
		}
	}
}
