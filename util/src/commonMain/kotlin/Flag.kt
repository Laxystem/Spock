package quest.laxla.spock

/**
 * Tracks the completion of an operation.
 *
 * After being [set], a flag cannot be unset.
 *
 * @since 0.0.1-alpha.4
 */
public interface Flag {
	/**
	 * Has this [Flag] been [set] yet?
	 *
	 * @since 0.0.1-alpha.4
	 */
	public operator fun invoke(): Boolean

	/**
	 * Marks this [Flag].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun set()
}
