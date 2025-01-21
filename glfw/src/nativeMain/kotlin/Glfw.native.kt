@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi
import quest.laxla.spock.RawSpockApi

@RawSpockApi
public actual fun glfwInit(): Unit? = Unit.takeIf { glfw.glfwInit() == GlfwTrue }

@RawSpockApi
public actual fun glfwTerminate() {
	glfw.glfwTerminate()
}

public actual val GlfwTrue: Int get() = glfw.GLFW_TRUE
public actual val GlfwFalse: Int get() = glfw.GLFW_FALSE
