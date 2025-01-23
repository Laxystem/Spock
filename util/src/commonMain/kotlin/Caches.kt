package quest.laxla.spock

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Creates a [Cache] that never [remove]s (and therefore closes) its content unless
 * [remove] is manually called or if the cache itself is [close][SuspendCloseable.close]d.
 *
 * The returned cache clears when [close][SuspendCloseable.close]d, and may be reused;
 * However, this is not recommended if the cache was passed by reference,
 * as other users can't know about the closure.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
public inline fun <Descriptor, Product> ManualCache(crossinline producer: suspend (Descriptor) -> Product): Cache<Descriptor, Product> =
	object : Cache<Descriptor, Product> {
		private val contents = hashMapOf<Descriptor, Product>()
		private val mutex = Mutex()

		override suspend fun get(descriptor: Descriptor): Product = mutex.withLock {
			contents.getOrPut(descriptor) { producer(descriptor) }
		}

		override suspend fun contains(descriptor: Descriptor): Boolean = mutex.withLock { descriptor in contents }

		private suspend fun close(removed: Product): Unit? = when (removed) {
			is SuspendCloseable -> removed.close()
			is AutoCloseable -> removed.close()
			null -> null
			else -> Unit
		}

		override suspend fun remove(descriptor: Descriptor): Unit? = mutex.withLock {
			contents.remove(descriptor)?.let { close(it) }
		}

		override suspend fun cacheAll(descriptors: Sequence<Descriptor>) = mutex.withLock {
			coroutineScope {
				descriptors.filter { it !in contents }.mapTo(mutableListOf()) {
					launch {
						contents.put(it, producer(it))
					}
				}.joinAll()
			}
		}

		override suspend fun close() = mutex.withLock {
			for (product in contents.values.reversed()) close(product)
			contents.clear()
		}
	}
