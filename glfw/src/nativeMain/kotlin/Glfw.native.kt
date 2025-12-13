@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi

internal expect fun _glfwInit(): Int

internal actual fun glfwInit(): Unit? = Unit.takeIf { _glfwInit() == GlfwTrue }

