package quest.laxla.spock

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Creates a [Cache] that'll create missing entries using the given [producer].
 *
 * The returned cache will never forget its contents unless [remove] is explicitly called
 * or if the cache itself is [close][SuspendCloseable.close]d.
 *
 * The returned cache is cleared when [close][SuspendCloseable.close]d, and may be reused.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
public inline fun <Descriptor, Product> MultiCache(crossinline producer: (Descriptor) -> Product): Cache<Descriptor, Product> =
	object : Cache<Descriptor, Product> {
		private val contents = linkedMapOf<Descriptor, Product>()
		private val mutex = Mutex()

		override suspend fun get(descriptor: Descriptor): Product = mutex.withLock {
			contents.getOrPut(descriptor) { producer(descriptor) }
		}

		override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLock { descriptor in contents }

		suspend fun close(removed: Product): Unit? = when (removed) {
			is SuspendCloseable -> removed.close()
			is AutoCloseable -> removed.close()
			null -> null
			else -> Unit
		}

		override suspend fun remove(descriptor: Descriptor): Unit? = mutex.withLock {
			contents.remove(descriptor)?.let { close(it) }
		}

		override suspend fun close() = mutex.withLock {
			for (product in contents.values.reversed()) close(product)
			contents.clear()
		}
	}
