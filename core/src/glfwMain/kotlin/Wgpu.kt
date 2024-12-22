@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package quest.laxla.spock

import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.Surface
import quest.laxla.spock.glfw.Window

public expect class Wgpu : AutoCloseable {
	override fun close()
}

internal expect fun createWgpu(): Wgpu?
public fun Wgpu(): Wgpu = createWgpu() ?: error("Failed creating wgpu instance")

private fun surfaceError(window: Window): Nothing = error("Failed creating surface for window $window")

public expect fun Wgpu.createSurfaceOrNull(window: Window, width: UInt, height: UInt): Surface?
public fun Wgpu.createSurface(window: Window, width: UInt, height: UInt): Surface =
	createSurfaceOrNull(window, width, height) ?: surfaceError(window)

public suspend fun Wgpu.createSurfaceOrNull(window: Window): Surface? {
	val (width, height) = window.size.await()

	return createSurfaceOrNull(window, width, height)
}

public suspend fun Wgpu.createSurface(window: Window): Surface = createSurfaceOrNull(window) ?: surfaceError(window)


public expect fun Wgpu.requestAdapterOrNull(surface: Surface): Adapter?
public fun Wgpu.requestAdapter(surface: Surface): Adapter =
	requestAdapterOrNull(surface) ?: error("Failed creating adapter for surface $surface")
