package quest.laxla.spock.glfw

/**
 * Exception thrown when an error is reported by [Glfw] itself.
 *
 * @since 0.0.1-alpha.4
 */
public open class GlfwException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
