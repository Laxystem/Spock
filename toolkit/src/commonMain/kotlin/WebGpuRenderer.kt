package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.TextureFormat
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.Renderer

/**
 * [Renderer] implemented using wgpu4k.
 *
 * It is not the renderer's responsibility to close [device] and [surface].
 *
 * @since 0.0.1-alpha.4
 */
@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface WebGpuRenderer : Renderer {
	/**
	 * The GPU used by this [WebGpuRenderer].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public val device: Device

	/**
	 * The surface onto which this renderer draws.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public val surface: Surface

	/**
	 * The [TextureFormat] of this renderer's output.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public val format: TextureFormat
}
