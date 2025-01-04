package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.SuspendCloseable

/**
 * @since 0.0.1-alpha.1
 */
public expect class Window(width: UInt, height: UInt, title: String) : SuspendCloseable {
	/**
	 * @since 0.0.1-alpha.1
	 */
	public val shouldClose: Boolean

	/**
	 * @since 0.0.1-alpha.1
	 */
	@ExperimentalSpockApi
	public val size: Deferred<Pair<UInt, UInt>>

	/**
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public inline fun setFramebufferSizeCallback(crossinline callback: Window.(width: UInt, height: UInt) -> Unit): Job
	
	override suspend fun close()
}
