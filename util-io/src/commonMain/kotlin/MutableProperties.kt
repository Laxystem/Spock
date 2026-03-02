package quest.laxla.spock.io

import kotlinx.io.Source
import quest.laxla.spock.growArraySize
import kotlin.experimental.and

// Some code in this file was stolen from the JDK.

private const val FormFeed = '\u000C'

private inline val Char.isSkippableWhitespace get() = this == ' ' || this == '\t' || this == FormFeed
private inline val Char.isNewline get() = this == '\r' || this == '\n'
private inline val Byte.isNewline get() = this == '\r'.code.toByte() || this == '\n'.code.toByte()
private inline val Char.isComment get() = this == '#' || this == '!'
private inline val Char.isSeparator get() = this == '=' || this == ':'

// TODO: replace ISO8859-1 with UTF-8 once supported by KotlinX IO
private class PropertiesLineReader(private val source: Source) {
	var lineBuffer = CharArray(1024)
	private val inputBuffer = ByteArray(8192)
	private var actualBufferSize = 0
	private var inputOffset = 0

	fun readLine(): Int {
		// Java uses local variables to optimize... we'll unwillingly cooperate.
		var logicalLineLength = 0
		var offset = inputOffset
		var actualBufferSize = actualBufferSize

		var skipWhitespace = true
		var lineAfterBackslash = false
		var precedingBackslash = false
		val input = inputBuffer
		var lines = lineBuffer
		var character: Char

		while (true) {
			if (offset >= actualBufferSize) {
				actualBufferSize = source.readAtMostTo(inputBuffer)
				this.actualBufferSize = actualBufferSize

				// if EOF...
				if (actualBufferSize <= 0) return when {
					logicalLineLength == 0 -> -1
					precedingBackslash -> logicalLineLength - 1
					else -> logicalLineLength
				}

				offset = 0
			}

			// same as decoding from ISO8859-1
			character = (input[offset++] and 0xFF.toByte()).toInt().toChar()

			if (skipWhitespace) when {
				character.isSkippableWhitespace -> continue
				!lineAfterBackslash && character.isNewline -> continue
				else -> {
					skipWhitespace = false
					lineAfterBackslash = false
				}
			}

			when {
				logicalLineLength == 0 && character.isComment -> {
					// we're still on a new logical line

					commentEating@ while (true) {
						var byte: Byte
						while (offset < actualBufferSize) {
							byte = input[offset++]

							// saves us a check in the common case of a non-newline
							if (byte.toInt() <= '\r'.code && byte.isNewline)
								break@commentEating
						}

						// buffer ended, reload it...
						if (offset == actualBufferSize) {
							actualBufferSize = source.readAtMostTo(input)
							this.actualBufferSize = actualBufferSize

							if (actualBufferSize <= 0) return -1 // EOF
							else offset = 0
						}
					}

					skipWhitespace = true
				}

				character.isNewline -> {
					// must be true here: !skipWhitespace || lineAfterBackslash

					if (logicalLineLength == 0) { // empty line
						// this exists to prevent a sequence of \[\n][\n] from becoming a 'line' of length 0
						skipWhitespace = true
						continue
					}

					if (offset >= actualBufferSize) {
						actualBufferSize = source.readAtMostTo(input)
						this.actualBufferSize = actualBufferSize

						offset = 0
						if (actualBufferSize <= 0)
							return if (precedingBackslash) logicalLineLength - 1
							else logicalLineLength
					}

					if (precedingBackslash) {
						// ending backslash is not part of the line
						logicalLineLength -= 1
						// skip leading whitespace characters on the following line
						skipWhitespace = true
						lineAfterBackslash = true
						precedingBackslash = false

						// skip a [\n] coming after a \[\r]
						if (character == '\r' && input[offset] == '\n'.code.toByte()) offset++
					} else {
						inputOffset = offset
						return logicalLineLength
					}
				}

				else -> {
					lines[logicalLineLength++] = character
					if (logicalLineLength == lines.size) {
						lines = lines.copyOf(logicalLineLength.growArraySize(1, logicalLineLength))
						lineBuffer = lines
					}

					precedingBackslash = character == '\\' && !precedingBackslash
				}
			}
		}
	}
}

private fun CharArray.sanitize(start: Int, length: Int, stringBuilder: StringBuilder = StringBuilder()): String {
	val end = start + length

	var offset = start
	while (offset < end) if (this[offset++] == '\\') break
	if (offset == end) // no backslashes
		return concatToString(start, length)

	stringBuilder.apply {
		clear()
		appendRange(this, start, --offset)
	}

	// line-ending backslashes were sanitized by PropertiesLineReader::readLine already, so we can:
	while (offset < end) when (val character = this[offset++]) {
		'\\' -> when (val character = this[offset++]) {
			'u' -> {
				if (offset + 4 > end) throw IllegalArgumentException("Malformed \\uxxxx encoding.")

				var value = 0
				repeat(4) {
					value = (value shl 4) + when (val character = this[offset++]) {
						in '0'..'9' -> character - '0'
						in 'a'..'f' -> character - 'a' + 10
						in 'A'..'F' -> character - 'A' + 10
						else -> throw IllegalArgumentException("Malformed \\uxxxx encoding.")
					}
				}

				value.toChar()
			}

			't' -> '\t'
			'r' -> '\r'
			'n' -> '\n'
			'f' -> FormFeed
			else -> character
		}

		else -> character
	}.let(stringBuilder::append)

	return stringBuilder.toString()
}

public fun MutableMap<String, String>.loadPropertiesFrom(source: Source) {
	val stringBuilder = StringBuilder()
	val reader = PropertiesLineReader(source)
	var lineLength: Int = reader.readLine()

	while (lineLength >= 0) {
		var valueStart = lineLength
		var hasSeparator = false
		var precedingBackslash = false

		var keyLength = 0
		while (keyLength < lineLength) {
			val character = reader.lineBuffer[keyLength]

			when {
				character.isSeparator && !precedingBackslash -> {
					valueStart = keyLength + 1
					hasSeparator = true
					break
				}

				character.isSkippableWhitespace && !precedingBackslash -> {
					valueStart = keyLength + 1
					break
				}

				character == '\\' -> precedingBackslash = !precedingBackslash
				else -> precedingBackslash = false
			}

			keyLength++
		}

		while (valueStart < lineLength) {
			val character = reader.lineBuffer[valueStart]
			if (!character.isSkippableWhitespace)
				if (hasSeparator || !character.isSeparator) break
				else hasSeparator = true

			valueStart++
		}

		put(
			key = reader.lineBuffer.sanitize(0, keyLength, stringBuilder),
			value = reader.lineBuffer.sanitize(valueStart, lineLength - valueStart, stringBuilder)
		)

		lineLength = reader.readLine()
	}
}
