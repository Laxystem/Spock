package quest.laxla.spock

@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Application : Closer {
	public suspend fun renderFrame()
}
