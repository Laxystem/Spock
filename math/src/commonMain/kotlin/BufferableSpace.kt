package quest.laxla.spock.math

import quest.laxla.spock.ByteAppender
import quest.laxla.spock.appendToByteString

/**
 * [Space] whose [Vector]s can be serialized into a [ByteString][kotlinx.io.bytestring.ByteString].
 *
 * @since 0.0.1-alpha.4
 */
public interface BufferableSpace<V> : SizedSpace<V> {
	/**
	 * Appends this [value] into this [appendToByteString].
	 *
	 * Concrete implementations are recommended to rename [value] to the concrete type [V] represents.
	 *
	 * @since 0.0.1-alpha.4
	 * @see kotlinx.io.bytestring.ByteString
	 */
	public fun ByteAppender.append(value: V)
}
