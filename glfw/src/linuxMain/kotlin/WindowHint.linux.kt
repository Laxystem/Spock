@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi

public actual fun glfwWindowHint(hint: Int, value: Int) {
	glfw.glfwWindowHint(hint, value)
}

public actual val GlfwClientApi: Int get() = glfw.GLFW_CLIENT_API
public actual val GlfwNoApi: Int get() = glfw.GLFW_NO_API

public actual val GlfwResizable: Int get() = glfw.GLFW_RESIZABLE
