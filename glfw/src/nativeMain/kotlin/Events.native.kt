@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import glfw.glfwPollEvents
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Job

public actual fun Glfw.pollEvents(): Job = launch {
	glfwPollEvents()
}
