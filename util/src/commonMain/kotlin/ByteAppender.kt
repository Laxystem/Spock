package quest.laxla.spock

import kotlinx.io.bytestring.ByteString

/**
 * Collects [Byte]s into a [ByteString].
 *
 * @since 0.0.1-alpha.4
 */
@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface ByteAppender {
	/**
	 * The number of bytes already written to this [ByteAppender].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val offset: Int

	/**
	 * The amount of [Byte]s this appender can store before extending or throwing an [IndexOutOfBoundsException].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val capacity: Int

	/**
	 * The number of bytes that can be added to this [ByteAppender] before it throws.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val remainingCapacity: Int

	/**
	 * Appends the given [byte] to this appender.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IndexOutOfBoundsException::class)
	public fun append(byte: Byte)

	/**
	 * Appends a subarray of [array] starting at [startIndexInclusive] and ending at [endIndexExclusive].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IndexOutOfBoundsException::class, IllegalArgumentException::class)
	public fun append(array: ByteArray, startIndexInclusive: Int = 0, endIndexExclusive: Int = array.size)

	/**
	 * Converts the contents of this appender to a [ByteString].
	 *
	 * This function is not guaranteed to return a [ByteString] with the same size as this appender's [capacity];
	 * Implementations may drop unused capacity.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun toByteString(): ByteString
}
