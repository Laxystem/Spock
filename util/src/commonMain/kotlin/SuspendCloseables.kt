package quest.laxla.spock

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Executes the given [block] function on this resource
 * and then closes it down correctly whether an exception is thrown or not.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.1
 * @see AutoCloseable.use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <T : SuspendCloseable?, R> T.use(block: (T) -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	var exception: Throwable? = null

	try {
		return block(this)
	} catch (e: Throwable) {
		exception = e
		throw e
	} finally {
		this.closeFinally(exception)
	}
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.1
 */
@PublishedApi
internal suspend fun SuspendCloseable?.closeFinally(cause: Throwable?): Unit = when {
	this == null -> {}
	cause == null -> close()
	else -> try {
		close()
	} catch (closeException: Throwable) {
		cause.addSuppressed(closeException)
	}
}

@JvmInline
private value class SuspendingAutoCloseable(val autoCloseable: AutoCloseable) : SuspendCloseable {
	override suspend fun close() = autoCloseable.close()
}

/**
 * Wraps this [AutoCloseable] as if it needed to `suspend`,
 * allowing APIs to only support [SuspendCloseable]s.
 *
 * @since 0.0.1-alpha.1
 */
public fun AutoCloseable.asSuspendCloseable() = this as? SuspendCloseable ?: SuspendingAutoCloseable(this)

public inline fun <T> holding(value: T, crossinline close: (T) -> Unit): ReadWriteProperty<Any?, T> =
	object : ReadWriteProperty<Any?, T> {
		private var _value: T = value

		override fun getValue(thisRef: Any?, property: KProperty<*>): T = _value

		override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
			val old = _value
			_value = value
			close(old)
		}
	}

@JvmName("holdingCloseable")
public fun <T : AutoCloseable> holding(value: T): ReadWriteProperty<Any?, T> =
	holding(value, AutoCloseable::close)
