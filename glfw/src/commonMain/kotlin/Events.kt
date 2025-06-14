package quest.laxla.spock.glfw

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {  }

public suspend fun Glfw.pollEvents() {
	logger.trace { "Polling GLFW events..." }

	pollEventsWithoutLogging()
}

internal expect suspend fun Glfw.pollEventsWithoutLogging()
