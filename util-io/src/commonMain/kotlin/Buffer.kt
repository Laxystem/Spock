package quest.laxla.spock.io

import kotlinx.io.Buffer
import kotlinx.io.write
import quest.laxla.spock.ByteString

/**
 * Creates a new [Buffer] of the provided [size] and writes [ByteString] into it.
 *
 * @since 0.0.1-alpha.4
 */
public fun ByteString.intoBuffer(): Buffer = Buffer().also {
	it.write(this)
}
