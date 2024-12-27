package quest.laxla.spock.glfw

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import org.lwjgl.glfw.GLFW.glfwPollEvents

private val logger = KotlinLogging.logger {  }

public actual fun Glfw.pollEvents(): Job = launch(start = CoroutineStart.ATOMIC) {
	glfwPollEvents()

	logger.debug { "Polling events..." }
}
