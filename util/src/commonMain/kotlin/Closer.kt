package quest.laxla.spock

/**
 * Collects resources and closes them cleanly in reversed order.
 *
 * @since 0.0.1-alpha.1
 * @see autoclose
 * @see Closer.plusAssign
 */
public interface Closer : SuspendCloseable {
	/**
	 * Remember to close this [AutoCloseable] together with this closer.
	 *
	 * @throws IllegalStateException if this closer is [close]d.
	 * @since 0.0.1-alpha.1
	 */
	@Throws(IllegalStateException::class)
	public operator fun <T> T.unaryPlus(): T where T : AutoCloseable

	/**
	 * Remember to close this [SuspendCloseable] together with this closer.
	 *
	 * @throws UnsupportedOperationException if a closer is attempted to be added to itself, or to another closer that contains it.
	 * @throws IllegalStateException if this closer is [close]d.
	 * @since 0.0.1-alpha.1
	 */
	@Throws(UnsupportedOperationException::class, IllegalStateException::class)
	public operator fun <T> T.unaryPlus(): T where T : SuspendCloseable

	/**
	 * Is [closeable] already remembered by this [Closer]?
	 *
	 * @since 0.0.1-alpha.1
	 */
	public operator fun contains(closeable: SuspendCloseable): Boolean
}
