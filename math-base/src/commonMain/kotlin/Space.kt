package quest.laxla.spock.math

/**
 * Details operations that can be performed on a [Vector].
 * 
 * @since 0.0.1-alpha.4
 */
public interface Space<V> {
	/**
	 * The basic unit of this [Space], usually `1`.
	 * 
	 * @since 0.0.1-alpha.4
	 * @see vectorOfOnes
	 */
	public val unit: V

	/**
	 * The center of axes of this [Space], usually `0`.
	 * 
	 * @since 0.0.1-alpha.4
	 * @see vectorOfZeros
	 */
	public val zero: V
}
