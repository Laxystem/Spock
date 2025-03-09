package quest.laxla.spock

import io.ygdrasil.webgpu.TextureFormat

/**
 * @since 0.0.1-alpha.4
 */
public interface FragmentShader : Shader {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val textureFormat: TextureFormat
}
