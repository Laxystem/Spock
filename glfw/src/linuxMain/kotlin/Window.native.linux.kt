package quest.laxla.spock.glfw

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar

@ExperimentalForeignApi
public actual typealias GLFWwindow = cnames.structs.GLFWwindow

@ExperimentalForeignApi
internal actual typealias GLFWmonitor = cnames.structs.GLFWmonitor

@ExperimentalForeignApi
internal actual fun glfwCreateWindow(
	width: Int,
	height: Int,
	title: String,
	monitor: CValuesRef<GLFWmonitor>?,
	share: CValuesRef<GLFWwindow>?
): CPointer<GLFWwindow>? = glfw.glfwCreateWindow(width, height, title, monitor, share)

@ExperimentalForeignApi
internal actual fun glfwWindowShouldClose(window: CPointer<GLFWwindow>): Int = glfw.glfwWindowShouldClose(window)

@ExperimentalForeignApi
internal actual fun glfwGetWindowSize(
	window: CPointer<GLFWwindow>,
	width: CPointer<IntVar>,
	height: CPointer<IntVar>
) {
	glfw.glfwGetWindowSize(window, width, height)
}

@ExperimentalForeignApi
internal actual fun glfwDestroyWindow(window: CPointer<GLFWwindow>) {
	glfw.glfwDestroyWindow(window)
}