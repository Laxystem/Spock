@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.toolkit

import ffi.NativeAddress
import io.ygdrasil.webgpu.NativeSurface
import kotlinx.cinterop.ExperimentalForeignApi
import quest.laxla.spock.glfw.GlfwWindow

internal actual fun Wgpu.getRawSurfaceOrNull(window: GlfwWindow): NativeSurface? = window.x11Display?.let { display ->
	window.x11Window?.let { window ->
		getSurfaceFromX11Window(NativeAddress(display), window)
	}
}
