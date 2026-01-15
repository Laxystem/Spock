package quest.laxla.spock.client.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.GPUDevice
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import quest.laxla.spock.*
import quest.laxla.spock.math.component1
import quest.laxla.spock.math.component2
import quest.laxla.spock.math.times
import kotlin.math.max

private val logger = KotlinLogging.logger { }

@OptIn(ExperimentalWasmJsInterop::class)
private suspend inline fun createResizeObserver(
	canvas: HTMLCanvasElement,
	crossinline render: suspend () -> Unit,
): Unit = coroutineScope {
	val observer = ResizeObserver { entries, _ -> // TODO: this isn't being triggered?
		for (entry in entries.toList()) {
			val (width, height) = entry.devicePixelContentBoxSize?.get(0)?.toVector2d()
				?: (entry.contentBoxSize!![0]!!.toVector2d() * window.devicePixelRatio)

			val target = entry.target as HTMLCanvasElement

			target.width = max(1, width.toInt())
			target.height = max(1, height.toInt())
		}

		@OptIn(DelicateCoroutinesApi::class)
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

private suspend fun Adapter.requestDeviceOrThrow(): GPUDevice = try {
	requestDevice().getOrThrow()
} catch (e: Throwable) {
	throw RuntimeException("Failed acquiring WebGPU device", e)
}

private suspend inline fun logExceptionsAndAutoclose(block: Closer.() -> Unit): Unit = try {
	autoclose(block = block)
} catch (e: Throwable) {
	logger.error(e) { "Uncaught Kotlin exception" }

	document.getElementById(CanvasElementId)?.parentElement?.apply {
		// language=html
		innerHTML = """
			<header><h1>Spock Engine: Uncaught Kotlin Exception</h1></header>
			<main>
			<ul>
			<li>If you don't know what this means, report to the game developer.</li>
			<li>See console for more details.</li>
			<li>Click on any <code>codeblock</code> to copy.</li>
			<li>Current platform: <a onclick='navigator.clipboard.writeText(text)'><code>${KTarget.current.toString().escapeHtml()}</code></a>.</li>
			<li>On Linux, install <a href='https://flathub.org/apps/com.google.ChromeDev'>Chrome Dev</a> and enable:
				<ul>
					<li><a onclick='navigator.clipboard.writeText(text)'><code>chrome://flags/#enable-unsafe-webgpu</code></a></li>
					<li><a onclick='navigator.clipboard.writeText(text)'><code>chrome://flags/#enable-vulkan</code></a></li>
				</ul>	
			</li>
			<li>Feel free to report issues or ask questions on <a href='https://codeberg.org/Laxystem/Spock/issues'>Codeberg</a>.</li>
			</ul>
			<h2>${e.message?.escapeHtml()}</h2>
			<a onclick='navigator.clipboard.writeText(text)'>
			<code style='white-space: pre-wrap'>${e.stackTraceToString().escapeHtml()}</code>
			</a>
			</main>
			
			<footer>
			<hr>
			<div style='display: flex'>
			<small style='flex: 1;'><i>This is a <a href='https://motherfuckingwebsite.com/'> motherfucking website</a>.</i></small>
			<div style='flex: 1; text-align: end'>🇮🇱🕊️🇵🇸</div>
			</div>
			</footer>
		""".trimIndent()
	}

	throw RuntimeException("An unknown error has occurred. Check console for more details.", e)
}

public actual suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (GPUDevice, Surface) -> WebGpuRenderer,
): Unit = logExceptionsAndAutoclose {
	document.title = title

	val adapter = +requestAdapterOrThrow()
	val device = +adapter.requestDeviceOrThrow()
	val canvas =
		document.getElementById(CanvasElementId) ?: error("Cannot find HTML canvas with id #$CanvasElementId")
	canvas as? HTMLCanvasElement ?: error("HTML element #$CanvasElementId must be a canvas")
	val surface = +Surface(canvas)
	val render = renderer(device, surface)

	render() // TODO: proper render loop...

	createResizeObserver(canvas, render::invoke)
}
