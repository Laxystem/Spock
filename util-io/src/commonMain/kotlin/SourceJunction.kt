package quest.laxla.spock.io

import kotlinx.io.IOException
import kotlinx.io.Source

private const val KotlinXioSegmentSize = 8192L

/**
 * Splits a [Source], so that it can be processed by several, downstream sources before being read fully.
 *
 * @since 0.0.1-alpha.4
 */
public interface SourceJunction : AutoCloseable {
	/**
	 * Splits a [Source] off of this junction.
	 *
	 * The source will output the same data regardless of what other sources split from this junction may or may not have read.
	 *
	 * @throws IllegalStateException when this junction is [solidified][solidify].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun split(): Source

	/**
	 * Removes at least 1, and up to [byteCount] bytes from this source and supplies them to downstream sources.
	 * Returns the number of bytes read, or -1 if this source is exhausted.
	 *
	 * @param byteCount the number of bytes to read.
	 *
	 * @throws IllegalArgumentException when [byteCount] is negative.
	 * @throws IllegalStateException when the source is closed.
	 * @throws IOException when some I/O error occurs.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun readAtMost(byteCount: Long): Long

	/**
	 * Are there no more bytes left to read?
	 *
	 * An equivalent to [Source.exhausted].
	 * The interface's default implementation assumes KotlinX IO's buffers' internal segment size is
	 * the optimal amount of bytes to read to perform this check.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val isExhausted: Boolean
		get() = readAtMost(KotlinXioSegmentSize) != -1L

	/**
	 * Stop [split]ting, and close when all existing splits are closed.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun solidify()
}
