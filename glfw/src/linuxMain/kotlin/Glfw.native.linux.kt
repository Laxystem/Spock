package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi

@ExperimentalForeignApi
internal actual fun _glfwInit(): Int = glfw.glfwInit()
