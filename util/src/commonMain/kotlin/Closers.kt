package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val logger = KotlinLogging.logger {}

/**
 * @author Laxystem
 */
@OptIn(ExperimentalSpockApi::class)
private class CloserImpl(
	closeables: Array<out SuspendCloseable>,
	override var subjectiveLogLevel: Level = Level.OFF
) : Closer, LocalizedLogLevel {
	private val suspendCloseables = mutableSetOf(*closeables)
	private val isClosed = Flag()
	private val closeMutex = Mutex()

	override fun <T> T.unaryPlus(): T where T : SuspendCloseable = also { suspendCloseable ->
		if (isClosed()) error("Cannot add $this into a closed closer.")

		if (this@CloserImpl === suspendCloseable || suspendCloseable is Closer && this@CloserImpl in suspendCloseable)
			throw UnsupportedOperationException("Cannot have closer close itself!")

		traceVia(logger) { "added $suspendCloseable to closer" }
		suspendCloseables += suspendCloseable
	}

	override fun <T> T.unaryPlus(): T where T : AutoCloseable = also { autoCloseable ->
		traceVia(logger) { "added $autoCloseable to closer" }
		suspendCloseables += autoCloseable.asSuspendCloseable()
	}

	override fun contains(closeable: SuspendCloseable): Boolean =
		closeable in suspendCloseables || suspendCloseables.any { it is Closer && closeable in it }

	override suspend fun close() = withContext(NonCancellable) {
		isClosed.setWithLock(closeMutex) {
			val exceptions = mutableSetOf<Throwable>()

			for (closeable in suspendCloseables.reversed()) try {
 				debugVia(logger) { "closing $closeable..." }
				closeable.close()
			} catch (e: Throwable) {
				debugVia(logger, e) { "Encountered exception while closing $closeable" }
				exceptions += e
			}

			suspendCloseables.clear()

			// return exception instead of throwing so that the setWithLock succeeds and the JVM doesn't forget the exception
			return@withContext when (exceptions.size) {
				0 -> null
				1 -> exceptions.single()
				else -> RuntimeException("Closer encountered exceptions when trying to close").apply {
					for (exception in exceptions) addSuppressed(exception)
				}
			}
		}

		null
	}.let { if (it != null) throw it }

	override fun toString(): String = if (isClosed()) "Closer(closed)" else "Closer($suspendCloseables)"
}

private fun Array<out AutoCloseable>.asSuspendCloseables() = Array(size) { index ->
	this[index].asSuspendCloseable()
}

/**
 * Creates a new [Closer] and adds [closeables] to it.
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(vararg closeables: SuspendCloseable, logLevel: Level = Level.OFF): Closer =
	CloserImpl(closeables, logLevel)

/**
 * Creates a new [Closer] and adds [closeables] to it.
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(vararg closeables: AutoCloseable, logLevel: Level = Level.OFF): Closer =
	CloserImpl(closeables.asSuspendCloseables(), logLevel)

/**
 * Create a new empty [Closer].
 *
 * @since 0.0.1-alpha.1
 */
public fun Closer(logLevel: Level = Level.OFF): Closer = CloserImpl(emptyArray<SuspendCloseable>(), logLevel)

/**
 * Creates a [Closer] scope and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.1
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(logLevel: Level = Level.OFF, block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(logLevel).use(block)
}

/**
 * Creates a [Closer] scope, adds all provided [closeables], and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.1
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(
	vararg closeables: SuspendCloseable,
	logLevel: Level = Level.OFF,
	block: Closer.() -> R
): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(*closeables, logLevel = logLevel).use(block)
}

/**
 * Creates a [Closer] scope, adds all provided [closeables], and automatically [closes][SuspendCloseable.close] it.
 *
 * @since 0.0.1-alpha.4
 * @see use
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(
	vararg closeables: AutoCloseable,
	logLevel: Level = Level.OFF,
	block: Closer.() -> R
): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(*closeables, logLevel = logLevel).use(block)
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
