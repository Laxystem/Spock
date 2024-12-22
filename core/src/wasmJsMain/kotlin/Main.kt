package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.wgpu.getSurface
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.max

private val logger = KotlinLogging.logger { }

public suspend fun main(): Unit = try {
	application(canvasElementId = "SpockTarget")
} catch (e: Throwable) {
	logger.error(e) { "Crashed!" }
	throw e
}

private suspend fun application(canvasElementId: String) = autoclose {
	val adapter = +requestAdapterOrThrow()
	val device = adapter.requestDevice() ?: error("Failed acquiring WebGPU device")
	val canvas = document.getElementById(canvasElementId) ?: error("Cannot find HTML canvas with id #$canvasElementId")
	canvas as? HTMLCanvasElement ?: error("HTML element #$canvasElementId must be a canvas")
	val surface = canvas.getSurface() ?: error("Failed acquiring WebGPU surface for HTML canvas #$canvasElementId")
	val application = +MyApplication(device, surface)

	logger.debug { "Rendering..." }

	application.renderFrame()

	coroutineScope {
		val observer = ResizeObserver { entries, _ ->
			for (entry in entries.toList()) {
				fun Int.toDevicePixels(): Int = (this * window.devicePixelRatio).toInt()

				val (width, height) = entry.devicePixelContentBoxSize?.get(0)?.run { inlineSize to blockSize }
					?: entry.contentBoxSize?.get(0)!!.run { inlineSize.toDevicePixels() to blockSize.toDevicePixels() }

				val target = entry.target as HTMLCanvasElement

				target.width = max(1, width)
				target.height = max(1, height)
			}

			launch(start = CoroutineStart.ATOMIC) {
				application.renderFrame()
			}
		}

		try {
			observer.observe(canvas, ResizeObserverOptions(DevicePixelContentBox))
		} catch (e: Throwable) {
			logger.warn(e) { "Failed registering ResizeObserver using device pixels, does your browser support it?" }
			observer.observe(canvas, ResizeObserverOptions(ContentBox))
		}
	}
}
