package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.Surface
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import quest.laxla.spock.Closer
import quest.laxla.spock.SuspendCloseable
import quest.laxla.spock.glfw.*

private val logger = KotlinLogging.logger {  }

internal expect suspend fun Closer.initializeWebGpu()

internal actual suspend fun Closer.webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (Device, Surface) -> WebGpuRenderer
) {
	+Glfw
	Glfw { logger.debug { "Initialized Glfw" } }
	
	initializeWebGpu()
	
	glfwWindowHint(GlfwResizable, GlfwFalse)
	glfwWindowHint(GlfwClientApi, GlfwNoApi)
	val window = +Window(preferredWidth, preferredHeight, title)
	val wgpu = +Wgpu()
	val surface = wgpu.createSurface(window)
	val adapter = +wgpu.requestAdapter(surface)
	val device = adapter.requestDevice() ?: error("Failed acquiring WebGPU device")
	val render = +renderer(device, surface)

	while (currentCoroutineContext().isActive && !window.shouldClose) {
		Glfw.pollEvents()
		render()
		surface.present()
		device.poll()
	}
}
