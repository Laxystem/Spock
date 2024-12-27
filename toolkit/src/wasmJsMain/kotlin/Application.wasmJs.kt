package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.Surface
import io.ygdrasil.wgpu.getSurface
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import quest.laxla.spock.Closer
import kotlin.math.max

private val logger = KotlinLogging.logger { }

private suspend inline fun Closer.createResizeObserver(
	canvas: HTMLCanvasElement,
	crossinline render: suspend () -> Unit,
): Unit = coroutineScope {
	val observer = ResizeObserver { entries, _ -> // TODO: this isn't being triggered?
		for (entry in entries.toList()) {
			fun Int.toDevicePixels(): Int = (this * window.devicePixelRatio).toInt()

			// TODO: replace below with a proper Vector2ui
			val (width, height) = entry.devicePixelContentBoxSize?.get(0)?.run { inlineSize to blockSize }
				?: entry.contentBoxSize?.get(0)!!.run { inlineSize.toDevicePixels() to blockSize.toDevicePixels() }

			val target = entry.target as HTMLCanvasElement

			target.width = max(1, width)
			target.height = max(1, height)
		}

		launch(start = CoroutineStart.ATOMIC) {
			render()
		}
	}
	
	try {
		logger.info { "Registering ResizeObserver..." }
		observer.observe(canvas, ResizeObserverOptions(DevicePixelContentBox))
	} catch (e: Throwable) {
		logger.warn(e) { "Failed registering ResizeObserver using device pixels, does your browser support it?" }
		observer.observe(canvas, ResizeObserverOptions(ContentBox))
	}
}

private const val CanvasElementId = "SpockTarget"

public actual suspend fun Closer.webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (Device, Surface) -> WebGpuRenderer,
) {
	document.title = title

	val adapter = +requestAdapterOrThrow()
	val device = adapter.requestDevice() ?: error("Failed acquiring WebGPU device")
	val canvas = document.getElementById(CanvasElementId) ?: error("Cannot find HTML canvas with id #$CanvasElementId")
	canvas as? HTMLCanvasElement ?: error("HTML element #$CanvasElementId must be a canvas")
	val surface = canvas.getSurface() ?: error("Failed acquiring WebGPU surface for HTML canvas #$CanvasElementId")
	val render = renderer(device, surface)
	
	render() // TODO: proper render loop...
	
	createResizeObserver(canvas, render::invoke)
}
