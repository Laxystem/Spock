package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList

@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Vertex {
	@ExperimentalSpockApi // TODO: replace with immutable array
	public val contents: ImmutableList<Float>
}
