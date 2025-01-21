package quest.laxla.spock

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author Laxystem
 */
@OptIn(ExperimentalSpockApi::class)
private class CloserImpl(vararg closeables: SuspendCloseable) : Closer {
	private val suspendCloseables = mutableSetOf(*closeables)
	private lateinit var closedMarker: Unit
	private val isClosed get() = ::closedMarker.isInitialized
	private val closeMutex = Mutex()

	override fun <T> T.unaryPlus(): T where T : SuspendCloseable = also { suspendCloseable ->
		if (isClosed) error("Cannot add $this into a closed closer.")

		if (this@CloserImpl === suspendCloseable || suspendCloseable is Closer && this@CloserImpl in suspendCloseable)
			throw UnsupportedOperationException("Cannot have closer close itself!")

		suspendCloseables += suspendCloseable
	}

	override fun <T> T.unaryPlus(): T where T : AutoCloseable = also { autoCloseable ->
		suspendCloseables += autoCloseable.asSuspendCloseable()
	}

	override fun contains(suspendCloseable: SuspendCloseable): Boolean = suspendCloseable in suspendCloseables

	override suspend fun close(): Unit = withContext(NonCancellable) {
		closeMutex.withLock {
			if (isClosed) return@withContext
			else closedMarker = Unit

			val exceptions = mutableSetOf<Throwable>()

			for (closeable in suspendCloseables.reversed()) try {
				closeable.close()
			} catch (e: Throwable) {
				exceptions += e
			}

			if (exceptions.isNotEmpty()) throw RuntimeException("Closer encountered exceptions when trying to close").apply {
				for (exception in exceptions) addSuppressed(exception)
			}
		}
	}
}

private fun Array<out AutoCloseable>.asSuspendCloseables() = Array(size) { index ->
	this[index].asSuspendCloseable()
}

/**
 * Create a new [Closer] and add [closeables] to it.
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(vararg closeables: SuspendCloseable): Closer = CloserImpl(*closeables)

/**
 * Creates a new [Closer] and adds [closeables] to it.
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(vararg closeables: AutoCloseable): Closer = Closer(*closeables.asSuspendCloseables())

/**
 * Create a new empty [Closer].
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(): Closer = CloserImpl(*emptyArray<SuspendCloseable>())

/**
 * Creates a [Closer] scope out of the provided closer and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.4
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> Closer.autoclose(block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return use {
		it.block()
	}
}

/**
 * Creates a [Closer] scope and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.1
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer().autoclose(block)
}

/**
 * Creates a [Closer] scope, adds all provided [closeables], and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.1
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(vararg closeables: SuspendCloseable, block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(*closeables).autoclose(block)
}

/**
 * Creates a [Closer] scope, adds all provided [closeables], and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.4
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(vararg closeables: AutoCloseable, block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(*closeables).autoclose(block)
}

/**
 * Remember to close [autoCloseable] together with this closer.
 *
 * @throws IllegalStateException if this closer is [close][Closer.close]d.
 * @since 0.0.1-alpha.1
 */
@Throws(IllegalStateException::class)
public operator fun Closer.plusAssign(autoCloseable: AutoCloseable) {
	autoCloseable.unaryPlus()
}

/**
 * Remember to close [suspendCloseable] together with this closer.
 *
 * @throws UnsupportedOperationException if a closer is attempted to be added to itself, or to another closer that contains it.
 * @throws IllegalStateException if this closer is [close][Closer.close]d.
 * @since 0.0.1-alpha.1
 */
@Throws(UnsupportedOperationException::class, IllegalStateException::class)
public operator fun Closer.plusAssign(suspendCloseable: SuspendCloseable) {
	suspendCloseable.unaryPlus()
}
