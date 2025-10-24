package quest.laxla.spock.init

import kotlinx.coroutines.CoroutineScope
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.KTarget
import quest.laxla.spock.current
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Invokes `kotlinx.coroutines.runBlocking` if supported on the current [KTarget].
 *
 * [KTarget.WasmJs] is **not** supported, and will always throw.
 * Instead, use a `suspend fun main`.
 *
 * @since 0.0.1-alpha.4
 */
public expect fun <T> runBlockingIfSupported(
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend CoroutineScope.() -> T
): T

@OptIn(ExperimentalSpockApi::class)
internal fun unsupported(function: String): Nothing =
	throw UnsupportedOperationException("Function $function is not supported on ${KTarget.current}, see its documentation for more information.")
