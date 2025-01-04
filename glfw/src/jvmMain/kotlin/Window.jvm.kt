package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import quest.laxla.spock.SuspendCloseable

public actual class Window(@LowLevelGlfwApi public val raw: Long) : SuspendCloseable {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(GLFW.glfwCreateWindow(width.toInt(), height.toInt(), title, NULL, NULL))

	@OptIn(LowLevelGlfwApi::class)
	public actual val shouldClose: Boolean get() = glfwWindowShouldClose(raw)

	@OptIn(LowLevelGlfwApi::class)
	public actual val size: Deferred<Pair<UInt, UInt>> = Glfw.async {
		MemoryStack.create().run {
			val width = mallocInt(1)
			val height = mallocInt(1)
			glfwGetWindowSize(raw, width, height)

			width.get().toUInt() to height.get().toUInt()
		}
	}

	@OptIn(LowLevelGlfwApi::class)
	public actual inline fun setFramebufferSizeCallback(crossinline callback: Window.(width: UInt, height: UInt) -> Unit): Job =
		Glfw.launch {
			GLFW.glfwSetFramebufferSizeCallback(raw) { _, width, height ->
				callback(width.toUInt(), height.toUInt())
			}
		}

	@OptIn(LowLevelGlfwApi::class)
	actual override suspend fun close(): Unit = Glfw {
		GLFW.glfwDestroyWindow(raw) // why sigsegv here??
	}
}
