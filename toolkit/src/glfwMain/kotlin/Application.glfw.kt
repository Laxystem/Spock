package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.ygdrasil.webgpu.Device
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import quest.laxla.spock.Closer
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.autoclose
import quest.laxla.spock.glfw.*

private val logger = KotlinLogging.logger {  }

internal expect suspend fun Closer.initializeWebGpu()

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalSpockApi::class)
public actual suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (Device, Surface) -> WebGpuRenderer
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

		logger.trace { "Polling device events..." }
		device.poll()

		logger.trace { "Rendering..." }
		render()

		logger.trace { "Presenting frame..." }
		surface.presentFrame()
	}

	logger.info { "Closing resources..." }
}.also {
	logger.debug { "Render loop ended" }
}
