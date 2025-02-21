package quest.laxla.spock

import kotlin.jvm.JvmInline

/**
 * Caches results of operations.
 *
 * Caches are thread-safe (usually via a [Mutex][kotlinx.coroutines.sync.Mutex])
 * and support closing both [SuspendCloseable]s and [AutoCloseable]s.
 * [Product]s implementing both are considered undefined behaviour.
 *
 * Caches are intended to be used inside other objects,
 * and passing them around by reference should be avoided.
 *
 * No closure order promises are made, as cached operations should be atomic,
 * that is, return the same (as in `==`) result given the same inputs.
 *
 * Caches are *not* required to return the same [Product]s on successive calls to [get] and [borrow].
 *
 * @since 0.0.1-alpha.4
 * @see Cache.Entry
 * @see InferringCache
 * @see EverlastingCache
 * @see PruningCache
 * @see SingleValueCache
 * @see NoopCache
 */
public interface Cache<in Descriptor, out Product> : SuspendCloseable {
	/**
	 * Checks if any [Product] is cached under this [descriptor].
	 *
	 * Implementations *may*, but are not required to count this query as a "usage"
	 * of the cached contents (e.g., for [tick]ing purposes).
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend operator fun contains(descriptor: Descriptor): Boolean

	/**
	 * Fetches a [Product] cached under this [descriptor], with details about how it should be [forgotten][forget].
	 *
	 * @since 0.0.1-alpha.4
	 * @see Cache.get
	 * @see Cache.Entry
	 */
	@RawSpockApi(replaceWith = ReplaceWith(expression = "this.get(descriptor)"))
	public suspend fun getEntry(descriptor: Descriptor): Entry<Product>

	/**
	 * Fetches a [Product] cached under this [descriptor], and removes it from this cache.
	 *
	 * This function revokes any relationship removed entries had with this cache,
	 * which would behave as if such entries never existed in the first place;
	 * thus, the responsibility to close entries returned by this function moves to the caller.
	 *
	 * @since 0.0.1-alpha.4
	 * @see borrow
	 */
	public suspend fun getAndRemove(descriptor: Descriptor): Entry<Product>

	/**
	 * Forgets all [Product]s cached under this [descriptor], and removes them from this [Cache].
	 *
	 * @return null if no [Product] was cached under this [descriptor].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun forget(descriptor: Descriptor): Unit?

	/**
	 * Informs the cache that [Product]s under the provided [descriptors] are about to be used.
	 *
	 * This operation prevents [tick]ing from removing contents,
	 * and may concurrently cache products.
	 *
	 * Calling this function is usually faster than not when dealing with large amounts of data,
	 * but it is not required, and **should not** be used for any side effects.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public suspend fun cacheAll(descriptors: Sequence<Descriptor>) {}

	/**
	 * Signifies to this cache that a "tick" has passed,
	 * and that it can safely perform any necessary maintenance.
	 *
	 * Caches may [forget] entries, produce new ones,
	 * or perform heavy calculations when this function is called.
	 *
	 * Implementations that delegate to other caches are expected to trigger *their* [tick] function as well.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun tick() {}

	/**
	 * Represents the existence of a [product] within a [Cache].
	 *
	 * Caches [close] their entries when forgetting them, and by doing so,
	 * may [close the product itself][Direct], [put it back into its original cache][Borrowed],
	 * or perform any other action as defined by the cache itself or passed to [InferringCache.put].
	 *
	 * @since 0.0.1-alpha.4
	 * @see CacheEntry
	 * @see Reference
	 */
	public sealed interface Entry<out T> : SuspendCloseable {
		/**
		 * The [Cache] product this entry represents.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val product: T

		@DelicateSpockApi
		override suspend fun close()

		/**
		 * [Cache.Entry] that was [borrow]ed from another cache and should be [put] back.
		 *
		 * @since 0.0.1-alpha.4
		 */
		@FutureValueClass
		public data class Borrowed<Descriptor, T>(
			public val cache: InferringCache<Descriptor, T>,
			public val original: Entry<T>
		) : Entry<T> {
			override val product: T get() = original.product

			@DelicateSpockApi
			override suspend fun close(): Unit = cache.put(original)
				?: throw IllegalStateException("Borrowed cache entry was not accepted back: $this")
		}

		/**
		 * [Cache.Entry] that [close]s forgotten [product]s.
		 *
		 * @since 0.0.1-alpha.4
		 * @see CacheEntry
		 */
		public sealed interface Direct<out T> : Entry<T> {
			/**
			 * @since 0.0.1-alpha.4
			 * @see Direct
			 * @see SuspendCloseable
			 */
			@JvmInline
			public value class Suspend<out T>(override val product: T) : Direct<T>, SuspendCloseable by product
					where T : SuspendCloseable

			/**
			 * @since 0.0.1-alpha.4
			 * @see Direct
			 * @see AutoCloseable
			 */
			@JvmInline
			public value class Auto<out T>(override val product: T) : Direct<T> where T : AutoCloseable {
				@DelicateSpockApi
				override suspend fun close() {
					product.close()
				}
			}
		}

		/**
		 * [Cache.Entry] that should be forgotten silently.
		 *
		 * @since 0.0.1-alpha.4
		 * @see CacheEntry
		 */
		@JvmInline
		public value class Reference<out T>(override val product: T) : Entry<T> {
			@DelicateSpockApi
			override suspend fun close() {
			}
		}
	}
}
