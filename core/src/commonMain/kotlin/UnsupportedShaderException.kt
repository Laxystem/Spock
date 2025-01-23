package quest.laxla.spock

/**
 * @since 0.0.1-alpha.4
 */
public open class UnsupportedShaderException(
	public val shader: Shader,
	message: String? = null,
	cause: Throwable? = null
) : IllegalArgumentException(shader.transpilationTraceToString() + message.letOrEmpty { ": $it" }, cause)
