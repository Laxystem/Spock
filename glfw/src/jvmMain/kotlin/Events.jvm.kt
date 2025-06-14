package quest.laxla.spock.glfw

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import org.lwjgl.glfw.GLFW.glfwPollEvents
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.ExperimentalSpockApi

private val logger = KotlinLogging.logger {  }

@OptIn(ExperimentalSpockApi::class, DelicateCoroutinesApi::class, DelicateSpockApi::class)
public actual suspend fun Glfw.pollEventsWithoutLogging()= Glfw {
	glfwPollEvents()
}
