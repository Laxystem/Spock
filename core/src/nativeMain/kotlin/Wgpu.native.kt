@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock

import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.Surface
import io.ygdrasil.wgpu.WGPU
import kotlinx.cinterop.ExperimentalForeignApi
import quest.laxla.spock.glfw.LowLevelGlfwApi
import quest.laxla.spock.glfw.Window
import webgpu.WGPUSurface

public actual typealias Wgpu = WGPU

internal actual fun createWgpu(): Wgpu? = WGPU.createInstance()

internal expect fun Wgpu.createRawSurface(window: Window): WGPUSurface?

@OptIn(LowLevelGlfwApi::class)
public actual fun Wgpu.createSurfaceOrNull(window: Window, width: UInt, height: UInt): Surface? = createRawSurface(window)?.let { raw ->
	Surface(raw) {
		width.toInt() to height.toInt()
	}
}

public actual fun Wgpu.requestAdapterOrNull(surface: Surface): Adapter? = requestAdapter(surface)?.also { adapter -> 
	surface.computeSurfaceCapabilities(adapter)
}
