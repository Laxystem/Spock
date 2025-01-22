package quest.laxla.spock.windowing

import kotlinx.coroutines.Deferred
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.SuspendCloseable
import quest.laxla.spock.math.Vector2ui

/**
 * @since 0.0.1-alpha.4
 */
public interface Window : SuspendCloseable {
	/**
	 * Was this window instructed by the operating system to close?
	 *
	 * @since 0.0.1-alpha.1
	 */
	public val shouldClose: Boolean

	/**
	 * The size of this window's drawable area.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@ExperimentalSpockApi
	public val size: Deferred<Vector2ui>
}
