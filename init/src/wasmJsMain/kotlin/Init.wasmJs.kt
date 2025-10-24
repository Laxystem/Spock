package quest.laxla.spock.init

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

@Deprecated(message = "Unsupported on WasmJS.") // not an error to allow regular compilation.
public actual fun <T> runBlockingIfSupported(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
	unsupported("runBlockingIfSupported")
