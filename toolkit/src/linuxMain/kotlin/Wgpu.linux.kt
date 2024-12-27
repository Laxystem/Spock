@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.toolkit

import kotlinx.cinterop.ExperimentalForeignApi
import quest.laxla.spock.glfw.Window
import webgpu.WGPUSurface

internal actual fun Wgpu.createRawSurface(window: Window): WGPUSurface? = window.x11Display?.let { display ->
	window.x11Window?.let { window ->
		getSurfaceFromX11Window(display, window)
	}
}
