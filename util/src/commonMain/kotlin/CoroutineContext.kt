package quest.laxla.spock

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 * Retrieves the current [CoroutineDispatcher] instance from the given [CoroutineContext] or
 * throws [IllegalStateException] if no job is present in the context.
 *
 * This method is a short-cut for `coroutineContext[CoroutineDispatcher]!!` and should be used only when it is known in advance that
 * the context does have a [CoroutineDispatcher] instance in it.
 *
 * @since 0.0.1-alpha.4
 */
//@get:Throws(IllegalStateException::class) TODO: crashes on native
public val CoroutineContext.dispatcher: CoroutineDispatcher
	get() = this[CoroutineDispatcher] ?: error("Coroutine context doesn't contain coroutine dispatcher: $this")
