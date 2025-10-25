package quest.laxla.spock.client

/**
 * @since 0.0.1-alpha.4
 */
public open class UnsupportedShaderException(
	public val shader: Shader,
	message: String? = null,
	cause: Throwable? = null
) : IllegalArgumentException(shader.transpilationTraceToString() + message?.let { ": $it" }.orEmpty(), cause)
