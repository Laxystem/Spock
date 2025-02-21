package quest.laxla.spock

import kotlinx.coroutines.CancellationException

/**
 * [Cache] whose [Descriptor]s can be inferred from its [Product]s.
 *
 * @since 0.0.1-alpha.4
 * @see put
 * @see Pool
 * @see NoopCache
 */
public interface InferringCache<Descriptor, Product> : Cache<Descriptor, Product> {
	/**
	 * Infers the [Descriptor] this [product] would be cached under.
	 *
	 * Implementations *must* support inferring descriptors for [Product]s that may not exist in their cache,
	 * and *must not* cache the [product] provided to this function if not already cached.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun descriptorOf(product: Product): Descriptor

	/**
	 * Adds the provided [entry] onto the cache.
	 *
	 * Implementations that do not support accepting [entries][entry] *at all* **must** [close][Cache.Entry.close] any provided [entry].
	 *
	 * @return `null` if the provided [entry] does not fulfill this cache's requirements, if any.
	 * @throws UnsupportedOperationException if the provided [entry] was [lent] from this cache.
	 * @since 0.0.1-alpha.4
	 * @see Cache.Entry
	 */
	@Throws(UnsupportedOperationException::class, CancellationException::class)
	@OptIn(DelicateSpockApi::class)
	public suspend fun put(entry: Cache.Entry<Product>): Unit? =
		if (this lent entry) throw UnsupportedOperationException(message = "Cannot lend an entry to a cache it was borrowed from: $entry")
		else entry.close()
}
