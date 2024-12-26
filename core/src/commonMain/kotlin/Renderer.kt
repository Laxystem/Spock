package quest.laxla.spock

@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Renderer : Closer {
	public suspend operator fun invoke()
}
