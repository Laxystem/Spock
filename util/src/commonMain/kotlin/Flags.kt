package quest.laxla.spock

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a new [Flag].
 *
 * The newly created flag is thread-safe, as once flags are set, they cannot be unset.
 *
 * @since 0.0.1-alpha.4
 */
public fun Flag(): Flag = object : Flag {
	private lateinit var flag: Unit
	override fun invoke(): Boolean = ::flag.isInitialized

	override fun set() {
		flag = Unit
	}
}

/**
 * Returns `this` value if the given [Flag] is set, `null` otherwise.
 *
 * @since 0.0.1-alpha.4
 */
public fun <T> T.takeIfSet(flag: Flag): T? = takeIf { flag() }

/**
 * Returns `this` value if and only if the given [Flag] is **not** set, else, `null`.
 *
 * @since 0.0.1-alpha.4
 */
public fun <T> T.takeUnlessSet(flag: Flag): T? = takeUnless { flag() }

/**
 * Executes [block] if this flag is set.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun Flag.ifSet(block: () -> Unit) {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}

	if (this()) block()
}

/**
 * If this [Flag] is not set, executes the given [action],
 * and if it succeeds, [set]s the flag.
 *
 * This function is not thread-safe.
 * Use [setWithLock] instead if you don't want [action] to execute twice.
 *
 * @return the thrown exception, if any.
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun Flag.set(action: () -> Unit): Throwable? {
	contract {
		callsInPlace(action, InvocationKind.AT_MOST_ONCE)
	}

	if (!this()) try {
		action()
		set()
	} catch (e: Throwable) {
		return e
	}

	return null
}

/**
 * If this [Flag] is not set, executes the given [action] under the given [mutex]'s lock,
 * and if its succeeds, [set]s the flag.
 *
 * @param owner Optional owner token for debugging.
 * When specified (non-`null` value) and the [mutex] is already locked with the same token (same identity),
 * this function throws [IllegalStateException].
 * @return the thrown exception, if any.
 * @since 0.0.1-alpha.4
 * @see set
 * @see Mutex.withLock
 */
public suspend inline fun Flag.setWithLock(mutex: Mutex, owner: Any? = null, action: () -> Unit): Throwable? =
	mutex.withLock(owner) {
		set(action)
	}

private fun Flag.throwIfClosed() = ifSet {
	throw UnsupportedOperationException("This resource was closed, and can no longer be used.")
}

/**
 * Throws an [UnsupportedOperationException] declaring this resource was closed if the given [Flag] is set.
 *
 * @since 0.0.1-alpha.4
 */
public fun SuspendCloseable.throwIfClosed(flag: Flag) {
	flag.throwIfClosed()
}

/**
 * Throws an [UnsupportedOperationException] declaring this resource was closed if the given [Flag] is set.
 *
 * @since 0.0.1-alpha.4
 */
public fun AutoCloseable.throwIfClosed(flag: Flag) {
	flag.throwIfClosed()
}
