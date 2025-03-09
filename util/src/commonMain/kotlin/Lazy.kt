package quest.laxla.spock

/**
 * @since 0.0.1-alpha.4
 */
@PublishedApi
@FutureErrorType
internal object Uninitialized

/**
 * @since 0.0.1-alpha.4
 */
@FutureSuspendProperty
public fun interface SuspendLazy<out T> : suspend () -> T

/**
 * Executes contents of [block] once, and only once.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see lazy
 */
public inline fun <T> suspendLazy(crossinline block: suspend () -> T): SuspendLazy<T> {
	var value: Any? = Uninitialized

	return SuspendLazy {
		if (value === Uninitialized) {
			value = block()
			require(value !== Uninitialized) { "Returning the fake null object to suspendLazy is a contract breach!" }
		}

		@Suppress("UNCHECKED_CAST")
		value as T
	}
}
