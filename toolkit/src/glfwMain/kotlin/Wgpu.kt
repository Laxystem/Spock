@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.DeviceDescriptor
import io.ygdrasil.webgpu.NativeSurface
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.glfw.GlfwWindow

/**
 * An instance of the wgpu WebGpu implementation, a [Surface] and [Adapter] factory.
 *
 * @since 0.0.1-alpha.1
 */
public expect class Wgpu : AutoCloseable {
	override fun close()
}

internal expect fun createWebGpuOrNull(): Wgpu?

/**
 * Creates a new [Wgpu] instance.
 *
 * @since 0.0.1-alpha.1
 */
public fun Wgpu(): Wgpu = createWebGpuOrNull() ?: error("Failed creating WebGpu instance")

internal expect fun Wgpu.getRawSurfaceOrNull(window: GlfwWindow): NativeSurface?

/**
 * Creates a new [Surface] for this [window].
 *
 * @since 0.0.1-alpha.4
 */
public fun Wgpu.createSurface(window: GlfwWindow): Surface = getRawSurfaceOrNull(window)?.let { raw ->
	Surface(raw, window)
} ?: error("Failed creating surface for window $window")

internal expect fun Wgpu.requestAdapter(surface: NativeSurface): Adapter?

/**
 * Requests an [Adapter] for this [surface] from the operating system.
 *
 * If successful, this operation calibrates the surface for the new [Adapter],
 * breaking any previous usages.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(RawSpockApi::class)
public fun Wgpu.requestAdapter(surface: Surface): Adapter = requestAdapter(surface.raw)?.also { adapter ->
	surface.raw.computeSurfaceCapabilities(adapter)
} ?: error("Failed creating WebGPU adapter for surface $surface")

/**
 * Requests a [Device] matching the provided descriptor from this [Adapter].
 *
 * @since 0.0.1-alpha.4
 */
public suspend fun Adapter.requestDeviceOrThrow(device: DeviceDescriptor = DeviceDescriptor()): Device =
	requestDevice(device) ?: error("Failed creating WebGPU device $device for adapter $this")
