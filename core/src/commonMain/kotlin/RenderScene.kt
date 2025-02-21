package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList


@ExperimentalSpockApi
public fun interface RenderScene {
	public operator fun invoke(): ImmutableList<Pipeline.Batch<*>>
}
