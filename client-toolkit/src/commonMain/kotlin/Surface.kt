package quest.laxla.spock.client.toolkit

import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.SurfaceTexture
import kotlinx.collections.immutable.ImmutableSet
import quest.laxla.spock.windowing.Window

/**
 * Represents a platform-specific surface (e.g., a window) onto which rendered images may be presented.
 *
 * @since 0.0.1-alpha.4
 */
public expect class Surface : AutoCloseable {
	/**
	 * The logical window onto which this [Surface] is drawn.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val window: Window

	/**
	 * The optimal [GPUTextureFormat] for the current system.
	 *
	 * Using any other format may incur overhead.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val preferredTextureFormat: GPUTextureFormat?

	/**
	 * All [GPUTextureFormat]s supported for this [Surface].
	 *
	 * Using a format not included in this set will result in an exception.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val supportedTextureFormats: ImmutableSet<GPUTextureFormat>

	/**
	 * All [alpha mode][CompositeAlphaMode]s supported by this [Surface].
	 *
	 * Using an alpha mode not included in this set will result in an exception.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val supportedAlphaModes: ImmutableSet<CompositeAlphaMode>

	/**
	 * Retrieves the texture to be drawn when [presentFrame] is called.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val currentTexture: SurfaceTexture

	/**
	 * Configures this [Surface] for a [given][SurfaceConfiguration.device] [Device][io.ygdrasil.webgpu.Device]
	 * and clears the screen to `(0,0,0,0)` (fully transparent black).
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun configure(@Suppress("unused") configuration: SurfaceConfiguration)

	/**
	 * Draws the [currentTexture] onto the screen.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun presentFrame()

	override fun close()
}
