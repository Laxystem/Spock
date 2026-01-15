package quest.laxla.spock.glfw

import kotlinx.coroutines.*
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.SuspendCloseable
import quest.laxla.spock.dispatcher
import quest.laxla.spock.glfw.Glfw.close
import quest.laxla.spock.glfw.Glfw.initialize
import quest.laxla.spock.minus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Manages GLFW initialization and closure thread-safely.
 *
 * @since 0.0.1-alpha.4
 * @see initialize
 * @see close
 */
public data object Glfw : CoroutineScope, SuspendCloseable {
	private var context: CoroutineContext? = null

	// @get:Throws(IllegalStateException::class) TODO: crashes on native
	override val coroutineContext: CoroutineContext get() = context ?: error("Glfw is not initialized")

	private suspend fun newCoroutineContext() =
		currentCoroutineContext().dispatcher + (context?.get(Job)?.takeIf { it.isActive } ?: SupervisorJob())

	private fun closeCoroutineContext(message: String) {
		context?.job?.cancel(message)
		context = null
	}

	/**
	 * Initializes [Glfw] thread-safely.
	 *
	 * On macOS, This function must be executed on the same thread as the main function.
	 *
	 * Glfw calls will be executed on the same dispatcher as this function is executed from.
	 *
	 * If GLFW is already initialized, new GLFW calls will be called on the current dispatcher instead of the previous one.
	 *
	 * @throws GlfwException if GLFW reported an initialization failure.
	 * @since 0.0.1-alpha.4
	 */
	@Throws(GlfwException::class, CancellationException::class)
	public suspend fun initialize() {
		val contextWasNull = context == null
		if (contextWasNull) context = newCoroutineContext()

		if (async { glfwInit() }.await() == null) {
			val error = GlfwException("Failed initializing GLFW")

			if (contextWasNull) closeCoroutineContext("Context was created as part of a failing GLFW initialization")
			else throw AssertionError(FailedInitializationWithExistingCoroutineContext).apply {
				addSuppressed(error)
			}

			throw error
		}
	}

	/**
	 * Steps into a [Glfw] context.
	 *
	 * Unlike [async], [launch], and other functions that use [Glfw] as a [CoroutineScope],
	 * this call is implemented via [withContext], and will be a child of the caller's [Job].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@DelicateCoroutinesApi
	@DelicateSpockApi
	public suspend operator fun <T> invoke(
		context: CoroutineContext = EmptyCoroutineContext,
		block: suspend CoroutineScope.() -> T
	): T = withContext(context + (coroutineContext - Job), block)

	/**
	 * Terminates [Glfw] and cancels all coroutines that used it.
	 *
	 * If [Glfw] is not initialized, this function has no effect.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@OptIn(DelicateCoroutinesApi::class, DelicateSpockApi::class)
	override suspend fun close() {
		try {
			Glfw(NonCancellable) { glfwTerminate() }
		} finally {
			closeCoroutineContext("GLFW was terminated")
		}
	}

	override fun toString(): String = "Glfw(coroutineContext=$coroutineContext)"
}

internal expect fun glfwInit(): Unit?
internal expect fun glfwTerminate()

/**
 * The integer [Glfw] treats as `true`.
 *
 * @since 0.0.1-alpha.4
 */
public expect val GlfwTrue: Int

/**
 * The integer [Glfw] treats as `false`.
 *
 * @since 0.0.1-alpha.4
 */
public expect val GlfwFalse: Int


private const val FailedInitializationWithExistingCoroutineContext =
	"GLFW initialization should not fail when a GLFW coroutine context already exists, " +
			"as presumably, GLFW is already initialized."
