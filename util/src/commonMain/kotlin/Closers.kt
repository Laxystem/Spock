package quest.laxla.spock

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private class CloserImpl(vararg closeables: SuspendCloseable) : Closer {
	private val suspendCloseables = mutableSetOf(*closeables)
	
	override fun <T> T.unaryPlus(): T where T : SuspendCloseable = also { suspendCloseable ->
		if (this@CloserImpl === suspendCloseable || suspendCloseable is Closer && this@CloserImpl in suspendCloseable)
			throw UnsupportedOperationException("Cannot have closer close itself!")

		suspendCloseables += suspendCloseable
	}

	override fun <T> T.unaryPlus(): T where T : AutoCloseable = also { autoCloseable ->
		suspendCloseables += autoCloseable.asSuspendCloseable()
	}
	
	override fun contains(suspendCloseable: SuspendCloseable): Boolean = suspendCloseable in suspendCloseables

	override suspend fun close(): Unit = withContext(NonCancellable) {
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

public fun Closer(vararg closeables: SuspendCloseable): Closer = CloserImpl(*closeables)

public fun Closer(): Closer = CloserImpl(*emptyArray<SuspendCloseable>())

@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer().use {
		it.block()
	}
}

@OptIn(ExperimentalContracts::class)
public suspend inline fun <R> autoclose(vararg closeables: SuspendCloseable, block: Closer.() -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return Closer(*closeables).use {
		it.block()
	}
}

public operator fun Closer.plusAssign(autoCloseable: AutoCloseable) {
	autoCloseable.unaryPlus()
}

public operator fun Closer.plusAssign(suspendCloseable: SuspendCloseable) {
	suspendCloseable.unaryPlus()
}
