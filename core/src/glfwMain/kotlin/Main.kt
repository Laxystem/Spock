package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import quest.laxla.spock.glfw.*

private val logger = KotlinLogging.logger { }

internal expect suspend fun Closer.afterGlfwInit()

public suspend fun main(): Unit = withContext(Dispatchers.Default) {
	autoclose(Glfw) {
		Glfw { logger.debug { "Initialized Glfw" } }
		afterGlfwInit()

		glfwWindowHint(GlfwResizable, GlfwFalse)
		glfwWindowHint(GlfwClientApi, GlfwNoApi)
		val window = +Window(800u, 600u, "My Window")
		val wgpu = +Wgpu()
		val surface = wgpu.createSurface(window)
		val adapter = +wgpu.requestAdapter(surface)
		val device = adapter.requestDevice() ?: error("Failed acquiring WebGPU device")
		val render = +MyRenderer(device, surface)

		while (coroutineContext.isActive && !window.shouldClose) {
			pollEvents()
			render()
			surface.present()
		}
	}
}
