package quest.laxla.spock.client

import kotlinx.coroutines.CancellationException
import quest.laxla.spock.Closer

/**
 * Renders [vertices][VertexKind] onto a screen, and collects objects to be closed together with itself.
 *
 * @see Closer
 * @see RenderScene
 * @since 0.0.1-alpha.4
 */
public interface Renderer : Closer {
	/**
	 * Renders a frame.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(UnsupportedShaderException::class, CancellationException::class)
	public suspend operator fun invoke()
}
