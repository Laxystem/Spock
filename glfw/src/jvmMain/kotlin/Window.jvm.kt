package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf
import quest.laxla.spock.windowing.Window
import kotlin.system.exitProcess

@OptIn(RawSpockApi::class)
public actual class GlfwWindow(@RawSpockApi public val raw: Long) : Window {
	public actual constructor(width: UInt, height: UInt, title: String) :
			this(GLFW.glfwCreateWindow(width.toInt(), height.toInt(), title, NULL, NULL))

	public actual override val shouldClose: Boolean get() = glfwWindowShouldClose(raw)

	@ExperimentalSpockApi
	public actual override val size: Deferred<Vector2ui> = Glfw.async {
		MemoryStack.create().run {
			val width = mallocInt(1)
			val height = mallocInt(1)
			glfwGetWindowSize(raw, width, height)

			UIntSpace.vectorOf(width.get().toUInt(), height.get().toUInt())
		}
	}

	@OptIn(ExperimentalSpockApi::class, DelicateCoroutinesApi::class, DelicateSpockApi::class)
	actual override suspend fun close(): Unit {
		Glfw.launch {
			println("closing window")
			GLFW.glfwDestroyWindow(raw)
			println("window closed")
		}
	}
}
