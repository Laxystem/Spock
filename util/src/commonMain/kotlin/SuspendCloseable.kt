package quest.laxla.spock

/**
 * A resource that can be closed or released and must `suspend` to do so.
 * 
 * @since 0.0.1-alpha.1
 * @see AutoCloseable
 */
public fun interface SuspendCloseable {
	/**
	 * Closes this resource.
	 *
	 * This function may throw, thus it is strongly recommended to use the [use] function instead.
	 *
	 * @since 0.0.1-alpha.1
	 * @see AutoCloseable.close
	 */
	public suspend fun close()
}
