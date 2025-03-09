package quest.laxla.spock

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * Fetches a [Product] cached under this [Descriptor].
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public suspend operator fun <Descriptor, Product> Cache<Descriptor, Product>.get(descriptor: Descriptor): Product =
	getEntry(descriptor).product

/**
 * Borrows a [Product] cached under this [descriptor] from this [Cache].
 *
 * When the returned [Cache.Entry] is closed, the product will be put back onto this cache;
 * Until then, it'll be treated as if it didn't exist in the first place.
 *
 * Note implementations are not required
 * to accept nor reject entries borrowed before the cache was closed and put back afterwards;
 * Verify the behaviour of such cases in your implementation's documentation.
 *
 * @since 0.0.1-alpha.4
 * @see Cache.getAndRemove
 */
public suspend fun <Descriptor, Product> InferringCache<Descriptor, Product>.borrow(descriptor: Descriptor): Cache.Entry.Borrowed<Descriptor, Product> =
	Cache.Entry.Borrowed(this, getAndRemove(descriptor))

/**
 * Retrieves the value of this [Cache] entry.
 *
 * @since 0.0.1-alpha.4
 */
public operator fun <T> Cache.Entry<T>.component1() = product

/**
 * Retrieves the operation that needs to be executed when forgetting this [Cache.Entry].
 *
 * @since 0.0.1-alpha.4
 */
@DelicateSpockApi
public operator fun <T> Cache.Entry<T>.component2(): SuspendCloseable = this

/**
 * Was this [entry]&nbsp;[borrow]ed from this [Cache]?
 *
 * @since 0.0.1-alpha.4
 */
public tailrec infix fun Cache<*, *>.lent(entry: Cache.Entry<*>): Boolean =
	entry is Cache.Entry.Borrowed<*, *> && (entry.cache === this || this lent entry.original)

/**
 * If `this` is [SuspendCloseable] or [AutoCloseable], closes it.
 *
 * This function accepts `Any?` as input as "close a `null` if you can"
 * is a valid operation that therefore should return [Unit].
 *
 * Classes that implement both [SuspendCloseable] and [AutoCloseable] are considered undefined behaviour.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see SuspendCloseable.close
 * @see AutoCloseable.close
 */
@Deprecated(level = DeprecationLevel.ERROR, message = "Replace with Cache.Entry.close")
@RawSpockApi
public suspend fun Any?.closeIfCloseable() {
	when (this) {
		is SuspendCloseable -> close()
		is AutoCloseable -> close()
	}
}

/**
 * Adds the provided [entry] onto this [Cache].
 *
 * @return `null` if the provided [entry] does not fulfill this cache's requirements, if any.
 * @throws UnsupportedOperationException if the provided [entry] was [lent] from this cache.
 * @since 0.0.1-alpha.4
 * @see InferringCache
 */
@OptIn(DelicateSpockApi::class)
@Throws(UnsupportedOperationException::class, CancellationException::class)
public suspend fun <Product> Cache<*, Product>.put(entry: Cache.Entry<Product>): Unit? =
	if (this is InferringCache<*, Product>) put(entry) else entry.close()

/**
 * Adds the provided [product] onto this [Cache].
 *
 * Products added with this function won't be affected if forgotten by the cache.
 *
 * @return `null` if the provided [product] does not fulfill this cache's requirements, if any.
 * @since 0.0.1-alpha.4
 * @see InferringCache
 * @see Cache.Entry.Reference
 */
public suspend fun <Product> Cache<*, Product>.putReferenceTo(product: Product): Unit? =
	put(Cache.Entry.Reference(product))

/**
 * Adds the provided [product] onto this [Cache].
 *
 * Products added with this function will be [closed][SuspendCloseable.close] if forgotten by the cache.
 *
 * @return `null` if the provided [product] does not fulfill this cache's requirements, if any.
 * @since 0.0.1-alpha.4
 * @see InferringCache
 * @see Cache.Entry.Direct
 */
public suspend fun <Product> Cache<*, Product>.putCloseable(product: Product): Unit? where Product : SuspendCloseable =
	put(Cache.Entry.Direct.Suspend(product))

/**
 * Adds the provided [product] onto this [Cache].
 *
 * Products added with this function will be [closed][AutoCloseable.close] if forgotten by the cache.
 *
 * @return `null` if the provided [product] does not fulfill this cache's requirements, if any.
 * @since 0.0.1-alpha.4
 * @see InferringCache
 * @see Cache.Entry.Direct
 */
public suspend fun <Product> Cache<*, Product>.putCloseable(product: Product): Unit? where Product : AutoCloseable =
	put(Cache.Entry.Direct.Auto(product))

/**
 * Creates a [Cache.Entry.Direct.Suspend], [Cache.Entry.Direct.Auto], or a [Cache.Entry.Reference],
 * depending on the provided [product]'s type.
 *
 * @since 0.0.1-alpha.4
 */
public fun <Product> CacheEntry(product: Product): Cache.Entry<Product> = when (product) {
	is SuspendCloseable -> Cache.Entry.Direct.Suspend(product)
	is AutoCloseable -> Cache.Entry.Direct.Auto(product)
	else -> Cache.Entry.Reference(product)
}

/**
 * Adds the provided [product] into the [Cache].
 *
 * @param autoclose should the provided [product] be closed when forgotten by this cache?
 * @return `null` if the provided [product] does not fill this cache's requirements, if any
 * @throws UnsupportedOperationException if the provided [product] was [lent] from this [Cache].
 * @since 0.0.1-alpha.4
 */
public suspend fun <Product> InferringCache<*, Product>.put(product: Product, autoclose: Boolean = true) =
	put(if (autoclose) CacheEntry(product) else Cache.Entry.Reference(product))

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
@OptIn(DelicateSpockApi::class)
@JvmSynthetic
@DelicateSpockApi
@PublishedApi
internal suspend fun <Product> MutableMap<*, Cache.Entry<Product>>.closeValuesAndClear() {
	for (product in values) product.close()
	clear()
}

/**
 * Creates a [Cache] that does not automatically [forget][Cache.forget] its contents.
 *
 * The returned cache clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be [put] back safely afterwards.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
@DelicateSpockApi
public inline fun <Descriptor, Product> EverlastingCache(
	crossinline producer: suspend (Descriptor) -> Cache.Entry<Product>,
	mutex: Mutex? = Mutex(),
	contents: MutableMap<Descriptor, Cache.Entry<Product>> = hashMapOf()
): Cache<Descriptor, Product> = object : Cache<Descriptor, Product> {
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		contents.getOrPut(descriptor) { producer(descriptor) }
	}

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		contents.remove(descriptor) ?: producer(descriptor)
	}

	override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLockIfAny { descriptor in contents }

	override suspend fun forget(descriptor: Descriptor): Unit? = mutex.withLockIfAny {
		contents[descriptor]?.close()
	}

	@ExperimentalSpockApi
	override suspend fun cacheAll(descriptors: Sequence<Descriptor>) = mutex.coroutineScopeWithLockIfAny {
		for (descriptor in descriptors) if (descriptor !in contents) launch {
			contents[descriptor] = producer(descriptor)
		}
	}

	override suspend fun close() = mutex.withLockIfAny {
		contents.closeValuesAndClear()
	}
}

/**
 * Creates a [Cache] that does not automatically [forget][Cache.forget] its contents.
 *
 * The returned cache clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(DelicateSpockApi::class, RawSpockApi::class)
public inline fun <Descriptor, Product> EverlastingCache(
	mutex: Mutex? = Mutex(),
	crossinline entryKind: (Product) -> Cache.Entry<Product> = ::CacheEntry,
	crossinline producer: suspend (Descriptor) -> Product
): Cache<Descriptor, Product> = EverlastingCache(producer = { entryKind(producer(it)) }, mutex)

/**
 * Creates a [Cache] that purges any contents unused for more than a single [tick][Cache.tick].
 *
 * The returned [Cache] clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
@OptIn(DelicateSpockApi::class)
@RawSpockApi
public inline fun <Descriptor, Product> PruningCache(
	crossinline producer: suspend (Descriptor) -> Cache.Entry<Product>,
	mutex: Mutex? = Mutex()
): Cache<Descriptor, Product> = object : Cache<Descriptor, Product> {
	var previousContents = hashMapOf<Descriptor, Cache.Entry<Product>>()
	var currentContents = hashMapOf<Descriptor, Cache.Entry<Product>>()

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		currentContents.getOrPut(descriptor) { previousContents.remove(descriptor) ?: producer(descriptor) }
	}

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		currentContents.remove(descriptor) ?: previousContents.remove(descriptor) ?: producer(descriptor)
	}

	override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLockIfAny {
		descriptor in currentContents ||
				previousContents.remove(descriptor)?.also { currentContents[descriptor] = it } != null
	}

	override suspend fun tick() = mutex.withLockIfAny {
		val emptyMap = previousContents.apply { closeValuesAndClear() }
		previousContents = currentContents
		currentContents = emptyMap
	}

	@DelicateSpockApi
	override suspend fun forget(descriptor: Descriptor): Unit? = mutex.withLockIfAny {
		when (descriptor) {
			in currentContents -> currentContents.remove(descriptor)?.close()
			in previousContents -> previousContents.remove(descriptor)?.close()
			else -> null
		}
	}

	@ExperimentalSpockApi
	override suspend fun cacheAll(descriptors: Sequence<Descriptor>): Unit = mutex.coroutineScopeWithLockIfAny {
		for (descriptor in descriptors) if (descriptor !in currentContents) launch {
			currentContents[descriptor] = previousContents.remove(descriptor) ?: producer(descriptor)
		}
	}

	override suspend fun close() = mutex.withLockIfAny {
		previousContents.closeValuesAndClear()
		currentContents.closeValuesAndClear()
	}
}

/**
 * Creates a [Cache] that purges any contents unused for more than a single [tick][Cache.tick].
 *
 * The returned [Cache] clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public inline fun <Descriptor, Product> PruningCache(
	mutex: Mutex? = Mutex(),
	crossinline entryKind: (Product) -> Cache.Entry<Product> = ::CacheEntry,
	crossinline producer: suspend (Descriptor) -> Product
): Cache<Descriptor, Product> = PruningCache(producer = { entryKind(producer(it)) }, mutex)

/**
 * Creates an [InferringCache] that can cache multiple [Product]s under a single [Descriptor].
 *
 * The returned [Cache] clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
@OptIn(DelicateSpockApi::class)
@PublishedApi
@RawSpockApi
internal inline fun <Descriptor, Product> Pool( // TODO: figure out how to expose this
	crossinline producer: suspend (Descriptor) -> Cache.Entry<Product>,
	crossinline inferrer: (Product) -> Descriptor,
	mutex: Mutex? = Mutex()
): InferringCache<Descriptor, Product> = object : InferringCache<Descriptor, Product> {
	private val contents = hashMapOf<Descriptor, HashSet<Cache.Entry<Product>>>()

	private val Descriptor.products get() = contents.getOrPut(this, ::hashSetOf)

	override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLockIfAny {
		!contents[descriptor].isNullOrEmpty()
	}

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		val products = descriptor.products

		if (products.isEmpty()) producer(descriptor).also { products += it }
		else products.first()
	}

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		val products = descriptor.products

		if (products.isEmpty()) producer(descriptor)
		else products.first().also { products -= it }
	}

	override suspend fun forget(descriptor: Descriptor): Unit? = mutex.withLockIfAny {
		val products = contents[descriptor]
		if (products.isNullOrEmpty()) return null

		for (product in products) product.close()
		products.clear()
	}

	@ExperimentalSpockApi
	override suspend fun cacheAll(descriptors: Sequence<Descriptor>) = mutex.coroutineScopeWithLockIfAny {
		for (descriptor in descriptors) if (contents[descriptor].isNullOrEmpty()) launch {
			descriptor.products += producer(descriptor)
		}
	}

	override fun descriptorOf(product: Product): Descriptor = inferrer(product)

	override suspend fun put(entry: Cache.Entry<Product>) = mutex.withLockIfAny {
		if (this lent entry) throw UnsupportedOperationException("Cannot lend an entry to a cache it was borrowed from: $entry")
		else inferrer(entry.product).products += entry
	}

	override suspend fun close() = mutex.withLockIfAny {
		for (set in contents.values) {
			for (product in set) product.close()
			set.clear()
		}
	}
}

/**
 * Creates an [InferringCache] that can cache multiple [Product]s under a single [Descriptor].
 *
 * The returned [Cache] clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public inline fun <Descriptor, Product> Pool(
	crossinline producer: suspend (Descriptor) -> Product,
	crossinline inferrer: (Product) -> Descriptor,
	crossinline entryKind: (Product) -> Cache.Entry<Product> = ::CacheEntry,
	mutex: Mutex = Mutex()
): InferringCache<Descriptor, Product> = Pool(
	producer = { descriptor: Descriptor -> entryKind(producer(descriptor)) },
	inferrer,
	mutex
)

/**
 * Creates an [InferringCache] that can cache multiple [Product]s under a single [Descriptor],
 * and clears entries unsued for more than a single [tick][Cache.tick].
 *
 * The returned [Cache] clears when [close][Cache.close], and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 * @author Laxystem
 */
@OptIn(DelicateSpockApi::class)
@RawSpockApi
@PublishedApi
internal inline fun <Descriptor, Product> PruningPool( // TODO: figure out how to expose this
	crossinline producer: suspend (Descriptor) -> Cache.Entry<Product>,
	crossinline inferrer: (Product) -> Descriptor,
	mutex: Mutex? = Mutex()
): InferringCache<Descriptor, Product> = object : InferringCache<Descriptor, Product> {
	var previousContents = hashMapOf<Descriptor, HashSet<Cache.Entry<Product>>>()
	var currentContents = hashMapOf<Descriptor, HashSet<Cache.Entry<Product>>>()

	val Descriptor.currentProducts: HashSet<Cache.Entry<Product>>
		get() = currentContents.getOrPut(this) { hashSetOf() }

	val Descriptor.previousProducts: HashSet<Cache.Entry<Product>>
		get() = previousContents.getOrPut(this) { hashSetOf() }

	val Descriptor.products: HashSet<Cache.Entry<Product>>
		get() {
			val currentProducts = currentProducts
			val previousProducts = previousProducts

			currentProducts.addAll(previousProducts)
			previousProducts.clear()

			return currentProducts
		}

	override fun descriptorOf(product: Product): Descriptor = inferrer(product)

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		val products = descriptor.products

		if (products.isEmpty()) producer(descriptor).also { products += it }
		else products.first()
	}

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		val products = descriptor.products

		if (products.isEmpty()) producer(descriptor) else products.first().also { products -= it }
	}

	override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLockIfAny {
		!currentContents[descriptor].isNullOrEmpty() || !previousContents[descriptor].isNullOrEmpty()
	}

	override suspend fun forget(descriptor: Descriptor) = mutex.withLockIfAny {
		val currentProducts = currentContents[descriptor]
		val previousProducts = previousContents[descriptor]

		var result: Unit? = null

		if (!currentProducts.isNullOrEmpty()) {
			for (product in currentProducts) product.close()
			currentProducts.clear()
			result = Unit
		}

		if (!previousProducts.isNullOrEmpty()) {
			for (product in previousProducts) product.close()
			previousProducts.clear()
			result = Unit
		}

		result
	}

	override suspend fun close() = mutex.withLockIfAny {
		for (set in previousContents.values) {
			for (entry in set) entry.close()
			set.clear()
		}

		for (set in currentContents.values) {
			for (entry in set) entry.close()
			set.clear()
		}
	}
}

/**
 * Creates an [InferringCache] that can cache multiple [Product]s under a single [Descriptor],
 * and clears entries unsued for more than a single [tick][Cache.tick].
 *
 * The returned [Cache] clears when [close][Cache.close], and may be reused;
 * Entries [borrowed][borrow] before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public inline fun <Descriptor, Product> PruningPool(
	crossinline producer: suspend (Descriptor) -> Product,
	crossinline inferrer: (Product) -> Descriptor,
	crossinline entryKind: (Product) -> Cache.Entry<Product> = ::CacheEntry,
	mutex: Mutex? = Mutex()
): InferringCache<Descriptor, Product> = PruningPool(
	producer = { entryKind(producer(it)) },
	inferrer,
	mutex
)

/**
 * @since 0.0.1-alpha.4
 */
@FutureErrorType
@PublishedApi
internal object Empty

/**
 * Creates a [Cache] that only remembers a single [Product] of a *single* [Descriptor],
 * forgetting values cached under previous descriptors.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
@OptIn(DelicateSpockApi::class)
@RawSpockApi
public inline fun <Descriptor, Product> SingleValueCache(
	mutex: Mutex? = Mutex(),
	crossinline producer: suspend (Descriptor) -> Cache.Entry<Product>
): Cache<Descriptor, Product> = object : Cache<Descriptor, Product> {
	private var currentDescriptor: Any? = Empty
	private var value: Cache.Entry<Product>? = null

	override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLockIfAny {
		currentDescriptor == descriptor
	}

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		if (currentDescriptor == descriptor) value!!
		else {
			currentDescriptor = descriptor
			value?.close()
			producer(descriptor).also { value = it }
		}
	}

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = mutex.withLockIfAny {
		if (currentDescriptor == descriptor) {
			currentDescriptor = Empty
			val result = value
			value = null
			return result!!
		} else producer(descriptor)
	}

	private suspend fun closeWithoutLock() {
		currentDescriptor = Empty
		value?.close()
		value = null
	}

	override suspend fun forget(descriptor: Descriptor) = mutex.withLockIfAny {
		if (currentDescriptor == descriptor) closeWithoutLock()
	}

	override suspend fun close() = mutex.withLockIfAny { closeWithoutLock() }
}

/**
 * Creates a [Cache] that only remembers a single [Product] of a *single* [Descriptor],
 * forgetting values cached under previous descriptors.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public suspend inline fun <Descriptor, Product> SingleValueCache(
	mutex: Mutex? = Mutex(),
	crossinline entryKind: (Product) -> Cache.Entry<Product>,
	crossinline producer: suspend (Descriptor) -> Product
): Cache<Descriptor, Product> = SingleValueCache(mutex) {
	entryKind(producer(it))
}

/**
 * Creates an [InferringCache] that does not cache, always calling the [producer] instead.
 *
 * @since 0.0.1-alpha.4
 */
public inline fun <Descriptor, Product> NoopCache(
	crossinline producer: suspend (Descriptor) -> Product,
	crossinline inferrer: (Product) -> Descriptor
): InferringCache<Descriptor, Product> = object : InferringCache<Descriptor, Product> {
	override suspend fun contains(descriptor: Descriptor): Boolean = false

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<Product> =
		Cache.Entry.Reference(producer(descriptor))

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<Product> = CacheEntry(producer(descriptor))
	override suspend fun forget(descriptor: Descriptor) {}

	override fun descriptorOf(product: Product): Descriptor = inferrer(product)

	override suspend fun close() {}
}


/**
 * Fetches a [Product] cached under the provided descriptors.
 *
 * @since 0.0.1-alpha.4
 * @see Cache.get
 */
public suspend operator fun <Descriptor1, Descriptor2, Product> Cache<Descriptor1, Cache<Descriptor2, Product>>.get(
	descriptor1: Descriptor1,
	descriptor2: Descriptor2
): Product = this[descriptor1][descriptor2]

/**
 * Fetches a [Product] cached under the provided descriptors, and removes it from this cache.
 *
 * [Product]s returned by this function are guaranteed not to be affected by ticks.
 *
 * @since 0.0.1-alpha.4
 * @see Cache.borrow
 */
public suspend fun <Descriptor1, Descriptor2, Product> Cache<Descriptor1, InferringCache<Descriptor2, Product>>.borrow(
	descriptor1: Descriptor1,
	descriptor2: Descriptor2
): Cache.Entry.Borrowed<in Descriptor2, out Product> = this[descriptor1].borrow(descriptor2)

/**
 * Fetches a [Product] cached under the provided descriptors.
 *
 * @since 0.0.1-alpha.4
 * @see Cache.get
 */
@JvmName("getDual")
public suspend operator fun <Descriptor1, Descriptor2, Product> Cache<Pair<Descriptor1, Descriptor2>, Product>.get(
	descriptor1: Descriptor1,
	descriptor2: Descriptor2
): Product = this[descriptor1 to descriptor2]

/**
 * Fetches a [Product] cached under the provided descriptors.
 *
 * @since 0.0.1-alpha.4
 * @see Cache.get
 */
@JvmName("borrowDual")
public suspend fun <Descriptor1, Descriptor2, Product> InferringCache<Pair<Descriptor1, Descriptor2>, Product>.borrow(
	descriptor1: Descriptor1,
	descriptor2: Descriptor2
): Cache.Entry.Borrowed<in Pair<Descriptor1, Descriptor2>, out Product> = borrow(descriptor1 to descriptor2)


/**
 * Creates an 'assembly line' of [Cache]s;
 * This prevents the creation of duplicated mid-products,
 * as instead, each step can be cached individually.
 *
 * When [close][SuspendCloseable.close]d or [tick][Cache.tick]ed,
 * the resulting cache will close (or tick) both provided caches.
 * For better control over this behaviour, it is recommended to use this function in combination with a [Closer],
 * like so:
 * ```kotlin
 * interface Foo : SuspendCloseable
 *
 * fun Foo(barCache: Cache<BarDescriptor, Bar>): Foo = object Foo : Closer by Closer() {
 *     // only the first cache will be closed
 *     val bars = +EverlastingCache<Foobar, BarDescriptor>(producer = foobar::toBarDescriptor()) then barCache
 *
 *     // etc
 * }
 * ```
 *
 * The returned cache has no state as of itself; It only operates on the caches provided to it.
 *
 * @since 0.0.1-alpha.4
 * @author Laxystem
 */
@OptIn(DelicateSpockApi::class)
public infix fun <Descriptor, MidProduct, FinalProduct> Cache<Descriptor, MidProduct>.then(
	cache: Cache<MidProduct, FinalProduct>
): Cache<Descriptor, FinalProduct> = object : Cache<Descriptor, FinalProduct> {
	private val baseCache get() = this@then

	override suspend fun contains(descriptor: Descriptor): Boolean = baseCache[descriptor] in cache

	@RawSpockApi
	override suspend fun getEntry(descriptor: Descriptor): Cache.Entry<FinalProduct> =
		cache.getEntry(baseCache[descriptor])

	override suspend fun getAndRemove(descriptor: Descriptor): Cache.Entry<FinalProduct> =
		cache.getAndRemove(baseCache[descriptor])

	override suspend fun forget(descriptor: Descriptor) {
		while (descriptor in baseCache) baseCache.getAndRemove(descriptor).apply { cache.forget(product) }.close()
	}

	@ExperimentalSpockApi
	override suspend fun cacheAll(descriptors: Sequence<Descriptor>) {
		baseCache.cacheAll(descriptors)
	}

	override suspend fun tick(): Unit = coroutineScope {
		launch { cache.tick() }
		launch { baseCache.tick() }
	}

	override suspend fun close(): Unit = coroutineScope {
		launch { cache.close() }
		launch { baseCache.close() }
	}
}

private fun <Descriptor, MidProduct, FinalProduct> Cache<Descriptor, MidProduct>.thenNonInferring(cache: Cache<MidProduct, FinalProduct>) =
	then(cache)

/**
 * Creates an 'assembly line' of [InferringCache]s;
 * This prevents the creation of duplicated mid-products, as instead, each step can be cached individually.
 *
 * When [close][SuspendCloseable.close]d, [tick][Cache.tick]ed,
 * the resulting cache will close (or tick) both provided caches.
 * For better control over this behaviour, it is recommended to use this function in combination with a [Closer],
 * like so:
 * ```kotlin
 * interface Foo : SuspendCloseable
 *
 * fun Foo(barCache: Cache<BarDescriptor, Bar>): Foo = object Foo : Closer by Closer() {
 *     // only the first cache will be closed
 *     val bars = +EverlastingCache<Foobar, BarDescriptor>(producer = foobar::toBarDescriptor()) then barCache
 *
 *     // etc
 * }
 * ```
 *
 * The returned cache has no state as of itself; It only operates on the caches provided to it.
 *
 * @since 0.0.1-alpha.4
 * @author Laxystem
 */
public infix fun <Descriptor, MidProduct, FinalProduct> InferringCache<Descriptor, MidProduct>.then(cache: InferringCache<MidProduct, FinalProduct>): InferringCache<Descriptor, FinalProduct> =
	object : InferringCache<Descriptor, FinalProduct>, Cache<Descriptor, FinalProduct> by thenNonInferring(cache) {
		private val baseCache get() = this@then

		override fun descriptorOf(product: FinalProduct): Descriptor =
			baseCache.descriptorOf(cache.descriptorOf(product))

		override suspend fun put(entry: Cache.Entry<FinalProduct>) = cache.put(entry)
	}

/**
 * Creates a [Cache] that [borrow]s values from another,
 * and [put]s back any content unused for more than a single [tick][Cache.tick].
 *
 * The returned cache clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries borrowed before closure can be put back safely afterwards.
 *
 * @param descriptorMapper maps this cache's [Descriptor] to the original's;
 * Allows borrowing products of the same descriptor for different objects.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public inline fun <Descriptor, MidDescriptor, Product> BorrowingCache(
	from: InferringCache<MidDescriptor, Product>,
	mutex: Mutex? = Mutex(),
	crossinline descriptorMapper: (Descriptor) -> MidDescriptor
): Cache<Descriptor, Product> = PruningCache({ from.getAndRemove(descriptorMapper(it)) }, mutex)

/**
 * Creates a [Cache] that [borrow]s values from another,
 * and [put]s back any content unused for more than a single [tick][Cache.tick].
 *
 * The returned cache clears when [close][SuspendCloseable.close]d, and may be reused;
 * Entries borrowed before closure can be put back safely afterwards.
 *
 * @since 0.0.1-alpha.4
 */
public fun <Descriptor, Product> BorrowingCache(
	from: InferringCache<Descriptor, Product>,
	mutex: Mutex? = Mutex()
): Cache<Descriptor, Product> = BorrowingCache(from, mutex) { it }
