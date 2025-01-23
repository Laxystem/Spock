package quest.laxla.spock

/**
 * Caches operations, and closes the created [Product]s in reverse order when [close]d.
 *
 * [Cache]s are thread-safe and support both [SuspendCloseable]s and [AutoCloseable]s.
 * [Product]s implementing both are considered undefined behaviour.
 *
 * @since 0.0.1-alpha.4
 */
public interface Cache<in Descriptor, Product> : SuspendCloseable {
	/**
	 * Fetches the [Product] cached under this [descriptor].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend operator fun get(descriptor: Descriptor): Product

	/**
	 * Checks if any [Product] is cached under this [descriptor].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend operator fun contains(descriptor: Descriptor): Boolean

	/**
	 * Closes the [Product] cached under this [descriptor], and removes it from this [Cache].
	 *
	 * Calling this function may be unsafe if [Product]s must be closed sequentially.
	 *
	 * @return null if no [Product] was cached under this [descriptor].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun remove(descriptor: Descriptor): Unit?

	/**
	 * Ensures all provided [descriptors] have a [Product] cached under them.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun cacheAll(descriptors: Sequence<Descriptor>)
}
