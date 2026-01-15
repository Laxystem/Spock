@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.client.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import quest.laxla.spock.logIfNull
import ffi.NativeAddress
import io.ygdrasil.webgpu.NativeSurface
import kotlinx.cinterop.ExperimentalForeignApi
import quest.laxla.spock.glfw.GlfwWindow

private val logger = KotlinLogging.logger { }

internal actual fun Wgpu.getRawSurfaceOrNull(window: GlfwWindow): NativeSurface? {
	val display = window.x11Display ?: return null
	val window = window.x11Window ?: return null

	return getSurfaceFromX11Window(NativeAddress(display), window).logIfNull(logger) {
		"Failed acquiring wgpu surface from GLFW window"
	}
}
