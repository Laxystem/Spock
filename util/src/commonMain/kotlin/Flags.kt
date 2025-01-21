package quest.laxla.spock

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
 * If this [Flag] is not set, executes [block], and if it succeeds, [set]s the flag.
 *
 * @return the thrown exception, if any.
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun Flag.set(block: () -> Unit): Throwable? {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}

	if (!this()) try {
		block()
		set()
	} catch (e: Throwable) {
		return e
	}

	return null
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
