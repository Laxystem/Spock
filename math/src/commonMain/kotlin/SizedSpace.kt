package quest.laxla.spock.math

/**
 * Space whose values' size is constant and known.
 *
 * @since 0.0.1-alpha.4
 */
public interface SizedSpace<V> : Space<V> {
	/**
	 * The size, in bytes, of a single value.
	 *
	 * @since 0.0.1-alpha.4
	 * @see sizeInBits
	 */
	public val sizeInBytes: UInt

	/**
	 * The size, in bits, of a single value.
	 *
	 * @since 0.0.1-alpha.4
	 * @see sizeInBytes
	 */
	public val sizeInBits: UInt
}
