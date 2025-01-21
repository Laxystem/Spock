package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.SuspendCloseable
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf

@OptIn(RawSpockApi::class)
public actual class Window(@RawSpockApi public val raw: Long) : SuspendCloseable {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(GLFW.glfwCreateWindow(width.toInt(), height.toInt(), title, NULL, NULL))

	public actual val shouldClose: Boolean get() = glfwWindowShouldClose(raw)

	public actual val size: Deferred<Vector2ui> = Glfw.async {
		MemoryStack.create().run {
			val width = mallocInt(1)
			val height = mallocInt(1)
			glfwGetWindowSize(raw, width, height)

			UIntSpace.vectorOf(width.get().toUInt(), height.get().toUInt())
		}
	}

	public actual inline fun setFramebufferSizeCallback(crossinline callback: Window.(width: UInt, height: UInt) -> Unit): Job =
		Glfw.launch {
			GLFW.glfwSetFramebufferSizeCallback(raw) { _, width, height ->
				callback(width.toUInt(), height.toUInt())
			}
		}

	actual override suspend fun close(): Unit = Glfw {
		GLFW.glfwDestroyWindow(raw) // why sigsegv here??
	}
}
