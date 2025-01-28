package quest.laxla.spock

import kotlinx.coroutines.CancellationException

/**
 * @since 0.0.1-alpha.4
 */
@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface Renderer : Closer {
	/**
	 * Renders a frame.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(UnsupportedShaderException::class, CancellationException::class)
	public suspend operator fun invoke()
}
