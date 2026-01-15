@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.DelicateCoroutinesApi
import quest.laxla.spock.DelicateSpockApi

@OptIn(DelicateCoroutinesApi::class, DelicateSpockApi::class)
public actual suspend fun Glfw.pollEventsWithoutLogging(): Unit = Glfw {
	glfwPollEvents()
}

@ExperimentalForeignApi
internal expect fun glfwPollEvents()
