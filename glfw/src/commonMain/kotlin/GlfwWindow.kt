package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.windowing.Window

/**
 * @since 0.0.1-alpha.4
 */
public expect class GlfwWindow(width: UInt, height: UInt, title: String) : Window {
	override val shouldClose: Boolean

	@ExperimentalSpockApi
	override val size: Deferred<Vector2ui>

	override suspend fun close()
}
