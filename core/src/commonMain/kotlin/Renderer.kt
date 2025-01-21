package quest.laxla.spock

/**
 * @since 0.0.1-alpha.4
 */
@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Renderer : Closer {
	/**
	 * Render a frame.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend operator fun invoke()
}
