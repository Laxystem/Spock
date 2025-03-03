package quest.laxla.spock

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Executes the given [action] within a [CoroutineScope] under this mutex's lock.
 *
 * @param owner Optional owner token for debugging. When `owner` is specified (non-null value) and this mutex
 *        is already locked with the same (as in `==`) token, this function throws an [IllegalStateException].
 *
 * @since 0.0.1-alpha.4
 * @see coroutineScope
 * @see Mutex.withLock
 */
@Throws(IllegalStateException::class, CancellationException::class)
@OptIn(ExperimentalContracts::class)
public suspend fun <R> Mutex.coroutineScopeWithLock(owner: Any? = null, action: suspend CoroutineScope.() -> R): R {
	contract {
		callsInPlace(lambda = action, kind = InvocationKind.EXACTLY_ONCE)
	}

	return withLock(owner) { coroutineScope(block = action) }
}
