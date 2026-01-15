package quest.laxla.spock.client.wgpu

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.poll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import quest.laxla.spock.Closer
import quest.laxla.spock.autoclose
import quest.laxla.spock.glfw.*

private val logger = KotlinLogging.logger {  }

internal expect suspend fun Closer.initializeWebGpu()

public actual suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (GPUDevice, Surface) -> WebGpuRenderer
): Unit = autoclose(Level.DEBUG) {
	(+Glfw).initialize()
	initializeWebGpu()
	
	glfwWindowHint(GlfwResizable, GlfwFalse)
	glfwWindowHint(GlfwClientApi, GlfwNoApi)
	val wgpu = +Wgpu()
	val window = +GlfwWindow(preferredWidth, preferredHeight, title)
	val surface = +wgpu.createSurface(window)
	val adapter = +wgpu.requestAdapter(surface)
	val device = +adapter.requestDeviceOrThrow()
	val render = +renderer(device, surface)

	while (currentCoroutineContext().isActive && !window.shouldClose) {
		Glfw.pollEvents()

		device.poll().onFailure {
			logger.error(it) { "Encountered error while polling device events" }
		}

		render()
		surface.presentFrame()
	}

	logger.info { "Closing resources..." }
}.also {
	logger.debug { "Render loop ended" }
}
