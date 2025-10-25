package quest.laxla.spock.client

import kotlinx.collections.immutable.ImmutableList

/**
 * Provides [Pipeline.Batch]es to a [Renderer].
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
public fun interface RenderScene {
	/**
	 * Invoked every render tick.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public operator fun invoke(): ImmutableList<Pipeline.Batch<*>>
}
