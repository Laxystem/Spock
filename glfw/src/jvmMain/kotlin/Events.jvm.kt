package quest.laxla.spock.glfw

import kotlinx.coroutines.DelicateCoroutinesApi
import org.lwjgl.glfw.GLFW.glfwPollEvents
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.ExperimentalSpockApi

@OptIn(ExperimentalSpockApi::class, DelicateCoroutinesApi::class, DelicateSpockApi::class)
public actual suspend fun Glfw.pollEventsWithoutLogging(): Unit = Glfw {
	glfwPollEvents()
}
