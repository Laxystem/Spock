package quest.laxla.spock.init

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

public actual fun <T> runBlockingIfSupported(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
	runBlocking(context, block)
