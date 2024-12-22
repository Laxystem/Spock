package quest.laxla.spock

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

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

public fun AutoCloseable.asSuspendCloseable() = this as? SuspendCloseable ?: SuspendingAutoCloseable(this)
