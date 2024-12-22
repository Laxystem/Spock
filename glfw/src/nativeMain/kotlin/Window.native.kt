@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.glfw

import cnames.structs.GLFWwindow
import glfw.glfwCreateWindow
import glfw.glfwDestroyWindow
import glfw.glfwGetWindowSize
import glfw.glfwWindowShouldClose
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.Deferred
import quest.laxla.spock.SuspendCloseable

public actual class Window(@LowLevelGlfwApi public val raw: CPointer<GLFWwindow>) : SuspendCloseable {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(glfwCreateWindow(width.toInt(), height.toInt(), title, null, null)!!)

	@OptIn(LowLevelGlfwApi::class)
	public actual val shouldClose: Boolean get() = glfwWindowShouldClose(raw) == GlfwTrue

	@OptIn(LowLevelGlfwApi::class)
	public actual val size: Deferred<Pair<UInt, UInt>> = Glfw.async {
		memScoped {
			val width = alloc<IntVar>()
			val height = alloc<IntVar>()
			glfwGetWindowSize(raw, width.ptr, height.ptr)

			width.value.toUInt() to height.value.toUInt()
		}
	}

	@OptIn(LowLevelGlfwApi::class)
	actual override suspend fun close(): Unit = Glfw {
		glfwDestroyWindow(raw)
	}
}
