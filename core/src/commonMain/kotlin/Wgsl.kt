package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableMap
import quest.laxla.spock.Shader as AnyShader
import quest.laxla.spock.Shader.Language as ShadingLanguage

/**
 * The WGSL shading language, developed for WebGPU.
 *
 * @since 0.0.1-alpha.4
 */
public data object Wgsl : ShadingLanguage {
	public interface Shader : AnyShader {
		/**
		 * The name of the function in this shader to be called by the GPU.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val entrypoint: String

		/**
		 * Constants passed to the GPU available in the shader.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val constants: ImmutableMap<String, Double>

		override val language: Wgsl get() = Wgsl
	}

	override fun accepts(shader: AnyShader): Boolean = shader is Wgsl.Shader
}
