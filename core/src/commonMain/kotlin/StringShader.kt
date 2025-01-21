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
}
