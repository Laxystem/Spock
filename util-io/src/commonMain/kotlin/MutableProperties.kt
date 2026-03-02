package quest.laxla.spock.io

import kotlinx.io.Source
import kotlinx.io.readLine
import quest.laxla.spock.growArraySize
import kotlin.experimental.and


private const val FormFeed = '\u000C'

// TODO: replace ISO8859-1 with UTF-8 once supported by KotlinX IO
private class PropertiesLineReader(private val source: Source) {
	var lineBuffer = CharArray(1024)
	private val inputBuffer = ByteArray(8192)
	private var actualBufferSize = 0
	private var inputOffset = 0

	private inline val Char.isSkippableWhitespace get() = this == ' ' || this == '\t' || this == FormFeed
	private inline val Char.isNewline get() = this == '\r' || this == '\n'
	private inline val Byte.isNewline get() = this == '\r'.code.toByte() || this == '\n'.code.toByte()
	private inline val Char.isComment get() = this == '#' || this == '!'

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

public fun MutableMap<String, String>.load(source: Source) = buildString {
	var limit: Int = source.readLine()
	var keyLength: Int
	var valueStart: Int
	var hasSeparator: Boolean
	var precedingBackslash: Boolean

	while ()
}