package quest.laxla.spock.glfw

import kotlinx.coroutines.*
import quest.laxla.spock.SuspendCloseable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.JsName
import kotlin.jvm.JvmName

// TODO: allow other dispatchers to use this thread, would need a custom dispatcher
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val glfwContext = newSingleThreadContext("Glfw")

public object Glfw : CoroutineContext by @OptIn(ExperimentalCoroutinesApi::class) glfwContext, SuspendCloseable {
	
	@PublishedApi
	internal val scope: CoroutineScope = CoroutineScope(Glfw + SupervisorJob())
	
	@PublishedApi
	internal suspend inline fun <R> CoroutineScope.execute(block: suspend CoroutineScope.() -> R): R {
		@OptIn(LowLevelGlfwApi::class)
		glfwInit() ?: error("Failed initializing GLFW")
		return block()
	}

	public inline fun <R> async(
		context: CoroutineContext = EmptyCoroutineContext,
		start: CoroutineStart = CoroutineStart.DEFAULT,
		crossinline block: suspend CoroutineScope.() -> R,
	): Deferred<R> = scope.async(context, start) {
		execute(block)
	}

	public suspend inline operator fun <R> invoke(
		context: CoroutineContext = EmptyCoroutineContext,
		crossinline block: suspend CoroutineScope.() -> R,
	): R = async(context, block = block).await()

	public inline fun launch(
		context: CoroutineContext = EmptyCoroutineContext,
		start: CoroutineStart = CoroutineStart.DEFAULT,
		crossinline block: suspend CoroutineScope.() -> Unit,
	): Job = scope.launch(context, start) {
		execute(block)
	}
	
	@JvmName("launchAndJoin")
	@JsName("launchAndAwait")
	public suspend inline operator fun invoke(
		context: CoroutineContext = EmptyCoroutineContext,
		crossinline block: suspend CoroutineScope.() -> Unit,
	): Unit = launch(context, block = block).join()

	override suspend fun close(): Unit = scope.launch { // doesn't use invoke to prevent init
		@OptIn(LowLevelGlfwApi::class)
		glfwTerminate()
	}.join()
}

@LowLevelGlfwApi
public expect fun glfwInit(): Unit?

@LowLevelGlfwApi
public expect fun glfwTerminate()

public expect val GlfwTrue: Int
public expect val GlfwFalse: Int
