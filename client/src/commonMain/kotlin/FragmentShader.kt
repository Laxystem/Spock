package quest.laxla.spock.client

import io.ygdrasil.webgpu.GPUTextureFormat

/**
 * @since 0.0.1-alpha.4
 */
public interface FragmentShader : Shader {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val textureFormat: GPUTextureFormat
}
