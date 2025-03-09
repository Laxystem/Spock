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
 * Executes the given [action] within a [CoroutineScope] under this [Mutex]'s lock.
 *
 * @param owner Optional owner token for debugging.
 * When [owner] is specified (non-`null` value) and this mutex is already locked with the same (as in `===`) token,
 * this function throws an [IllegalStateException].
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

/**
 * Executes the given [action], and if this [Mutex] is not `null`, under its lock.
 *
 * @param owner Optional owner token for debugging.
 * When [owner] is specified (non-`null` value) and this mutex is already locked with the same (as in `===`) token,
 * this function throws an [IllegalStateException].
 *
 * @since 0.0.1-alpha.4
 * @see Mutex.withLock
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> Mutex?.withLockIfAny(owner: Any? = null, action: () -> R): R {
	contract {
		callsInPlace(action, InvocationKind.EXACTLY_ONCE)
	}

	return if (this == null) action() else withLock(owner, action)
}

/**
 * Executes the given [action] within a [CoroutineScope], and if this [Mutex] is not `null`, under its lock.
 *
 * @param owner Optional owner token for debugging.
 * When [owner] is specified (non-`null` value) and this mutex is already locked with the same (as in `===`) token,
 * this function throws an [IllegalStateException].
 *
 * @since 0.0.1-alpha.4
 * @see coroutineScope
 * @see Mutex.withLock
 * @see Mutex.withLockIfAny
 */
@OptIn(ExperimentalContracts::class)
public suspend fun <R> Mutex?.coroutineScopeWithLockIfAny(owner: Any? = null, action: suspend CoroutineScope.() -> R): R {
	contract {
		callsInPlace(action, InvocationKind.EXACTLY_ONCE)
	}

	return withLockIfAny(owner) { coroutineScope(block = action) }
}
