package quest.laxla.spock.math

import kotlinx.io.bytestring.ByteStringBuilder

/**
 * [Space] whose [Vector]s can be serialized into a [ByteString][kotlinx.io.bytestring.ByteString].
 *
 * @since 0.0.1-alpha.4
 */
public interface BufferableSpace<V> : SizedSpace<V> {
	/**
	 * Appends this [value] into [ByteStringBuilder].
	 *
	 * Concrete implementations are recommended to rename [value] to the concrete type [V] represents.
	 *
	 * @since 0.0.1-alpha.4
	 * @see kotlinx.io.bytestring.ByteString
	 */
	public fun ByteStringBuilder.append(value: V)
}
