package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList

@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Mesh {
	@ExperimentalSpockApi
	public val indices: ImmutableList<Int> // TODO: replace with immutable arrays
	
	@ExperimentalSpockApi
	public val vertices: ImmutableList<Float> // TODO: support more vertex types
}
