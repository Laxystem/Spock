@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import glfw.GLFW_CLIENT_API
import glfw.GLFW_NO_API
import glfw.GLFW_RESIZABLE
import glfw.glfwWindowHint
import kotlinx.cinterop.ExperimentalForeignApi

public actual fun glfwWindowHint(hint: Int, value: Int) {
	glfwWindowHint(hint, value)
}

public actual val GlfwClientApi: Int get() = GLFW_CLIENT_API
public actual val GlfwNoApi: Int get() = GLFW_NO_API

public actual val GlfwResizable: Int get() = GLFW_RESIZABLE
