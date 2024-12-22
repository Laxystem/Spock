package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

private val logger = KotlinLogging.logger {  }

public fun mainBreaking(): Unit = try {
	runBlocking(Dispatchers.Default) {
		main()
	}
} catch (e: Throwable) {
	logger.error(e) { "Crashed!" }
}

internal actual suspend fun Closer.afterGlfwInit() = Unit
