package quest.laxla.spock.client.wgpu

import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUTextureFormat
import quest.laxla.spock.client.Renderer

/**
 * [Renderer] implemented using wgpu4k.
 *
 * It is not the renderer's responsibility to close [device] and [surface].
 *
 * @since 0.0.1-alpha.4
 */
public interface WebGpuRenderer : Renderer {
	/**
	 * The GPU used by this [WebGpuRenderer].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val device: GPUDevice

	/**
	 * The surface onto which this renderer draws.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val surface: Surface

	/**
	 * The [GPUTextureFormat] of this renderer's output.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val format: GPUTextureFormat
}
