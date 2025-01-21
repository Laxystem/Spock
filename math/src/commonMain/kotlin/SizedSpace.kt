package quest.laxla.spock.math

/**
 * Space whose values' size is constant and known.
 *
 * @since 0.0.1-alpha.4
 */
public interface SizedSpace<V> : Space<V> {
	public val sizeInBytes: UInt
	public val sizeInBits: UInt
}
