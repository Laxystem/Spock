@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import cnames.structs.GLFWwindow
import glfw.glfwCreateWindow
import glfw.glfwDestroyWindow
import glfw.glfwGetWindowSize
import glfw.glfwWindowShouldClose
import kotlinx.cinterop.*
import kotlinx.coroutines.Deferred
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf
import quest.laxla.spock.windowing.Window

@OptIn(RawSpockApi::class)
public actual class GlfwWindow(@RawSpockApi public val raw: CPointer<GLFWwindow>) : Window {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(glfwCreateWindow(width.toInt(), height.toInt(), title, null, null)!!)

	public actual override val shouldClose: Boolean get() = glfwWindowShouldClose(raw) == GlfwTrue

	@ExperimentalSpockApi
	public actual override val size: Deferred<Vector2ui> = Glfw.async {
		memScoped {
			val width = alloc<IntVar>()
			val height = alloc<IntVar>()
			glfwGetWindowSize(raw, width.ptr, height.ptr)

			UIntSpace.vectorOf(width.value.toUInt(), height.value.toUInt())
		}
	}

	actual override suspend fun close(): Unit = Glfw {
		glfwDestroyWindow(raw)
	}
}
