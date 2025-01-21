package quest.laxla.spock


public interface RenderScene<Vertex : Any> {
	@OptIn(ExperimentalSpockApi::class)
	public val pipelines: Sequence<Pipeline<Vertex>>
}
