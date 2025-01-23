package quest.laxla.spock

/**
 * Non-compiled [Shader], that is passed as a [String][kotlin.String].
 *
 * @since 0.0.1-alpha.4
 */
public interface StringShader : Shader {
	/**
	 * The shader's non-compiled code, written in [language].
	 *
	 * There's no performance penalty to different [Shader]s sharing the same code,
	 * as it is the [Renderer]'s responsibility to handle such situations.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val code: String

	override val formFactor: FormFactor get() = StringShader

	/**
	 * @author Laxystem
	 * @since 0.0.1-alpha.4
	 */
	public companion object FormFactor : Shader.FormFactor {
		override fun accepts(shader: Shader): Boolean = shader is StringShader

		override fun toString(): String = "StringShader"

		override fun equals(other: Any?): Boolean = other is FormFactor

		/**
		 * Kotlin [gives data objects constant hash codes](https://kotlinlang.org/docs/object-declarations.html#data-objects);
		 * This is the only way to reproduce this,
		 * as [FormFactor] itself cannot be made into a data object since it is a companion object.
		 *
		 * @author Laxystem
		 */
		private data object HashCodeImpl

		override fun hashCode(): Int = HashCodeImpl.hashCode()
	}
}
