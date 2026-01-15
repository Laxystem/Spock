@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import kotlinx.cinterop.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf
import quest.laxla.spock.windowing.Window

@ExperimentalForeignApi
public expect class GLFWwindow : CPointed

@ExperimentalForeignApi
internal expect class GLFWmonitor : CPointed

@ExperimentalForeignApi
internal expect fun glfwCreateWindow(
	width: Int,
	height: Int,
	title: String,
	monitor: CValuesRef<GLFWmonitor>?,
	share: CValuesRef<GLFWwindow>?
): CPointer<GLFWwindow>?

@ExperimentalForeignApi
internal expect fun glfwWindowShouldClose(window: CPointer<GLFWwindow>): Int

@ExperimentalForeignApi
internal expect fun glfwGetWindowSize(
	window: CPointer<GLFWwindow>,
	width: CPointer<IntVar>,
	height: CPointer<IntVar>
)

@ExperimentalForeignApi
internal expect fun glfwDestroyWindow(window: CPointer<GLFWwindow>)

public actual class GlfwWindow(public val pointer: CPointer<GLFWwindow>) : Window {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(glfwCreateWindow(width.toInt(), height.toInt(), title, null, null)!!)

	public actual override val shouldClose: Boolean get() = glfwWindowShouldClose(pointer) == GlfwTrue

	@ExperimentalSpockApi
	public actual override val size: Deferred<Vector2ui> = Glfw.async {
		memScoped {
			val width = alloc<IntVar>()
			val height = alloc<IntVar>()
			glfwGetWindowSize(pointer, width.ptr, height.ptr)

			UIntSpace.vectorOf(width.value.toUInt(), height.value.toUInt())
		}
	}

	@OptIn( DelicateCoroutinesApi::class, DelicateSpockApi::class)
	actual override suspend fun close(): Unit = Glfw(NonCancellable) {
		glfwDestroyWindow(pointer)
	}
}
