@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import cnames.structs.GLFWwindow
import glfw.glfwCreateWindow
import glfw.glfwDestroyWindow
import glfw.glfwGetWindowSize
import glfw.glfwWindowShouldClose
import kotlinx.cinterop.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.SuspendCloseable
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf

@OptIn(RawSpockApi::class)
public actual class Window(@RawSpockApi public val raw: CPointer<GLFWwindow>) : SuspendCloseable {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(glfwCreateWindow(width.toInt(), height.toInt(), title, null, null)!!)

	public actual val shouldClose: Boolean get() = glfwWindowShouldClose(raw) == GlfwTrue

	public actual val size: Deferred<Vector2ui> = Glfw.async {
		memScoped {
			val width = alloc<IntVar>()
			val height = alloc<IntVar>()
			glfwGetWindowSize(raw, width.ptr, height.ptr)

			UIntSpace.vectorOf(width.value.toUInt(), height.value.toUInt())
		}
	}
	public actual inline fun setFramebufferSizeCallback(crossinline callback: Window.(width: UInt, height: UInt) -> Unit): Job =
		Glfw.launch {

			TODO()
			/*glfw.glfwSetFramebufferSizeCallback(
				raw,
				staticCFunction { raw, width: Int, height: Int -> Window(raw!!).callback(width.toUInt(), height.toUInt()) }
			)*/
		}

	actual override suspend fun close(): Unit = Glfw {
		glfwDestroyWindow(raw)
	}
}
