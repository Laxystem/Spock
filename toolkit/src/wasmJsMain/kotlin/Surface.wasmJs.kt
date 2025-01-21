package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.w3c.dom.HTMLCanvasElement
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf

private val SupportedTextureFormats = persistentSetOf(
	TextureFormat.BGRA8Unorm,
	TextureFormat.RGBA8Unorm,
	TextureFormat.RGBA16Float
)

private val SupportedAlphaModes = persistentSetOf(
	CompositeAlphaMode.Opaque,
	CompositeAlphaMode.Premultiplied
)

private val scope = CoroutineScope(Dispatchers.Default)

@OptIn(RawSpockApi::class)
public actual class Surface(@RawSpockApi public val raw: CanvasSurface) : AutoCloseable {
	public actual val size: Deferred<Vector2ui> = scope.async { UIntSpace.vectorOf(raw.width, raw.height) }

	public actual val preferredTextureFormat: TextureFormat? = raw.preferredCanvasFormat

	public actual val supportedTextureFormats: ImmutableSet<TextureFormat> = SupportedTextureFormats
	public actual val supportedAlphaModes: ImmutableSet<CompositeAlphaMode> = SupportedAlphaModes

	public actual val currentTexture: SurfaceTexture = raw.getCurrentTexture()

	/**
	 * Create a new [quest.laxla.spock.toolkit.Surface] for this [canvas].
	 *
	 * @since 0.0.1-alpha.4
	 * @see HTMLCanvasElement.getCanvasSurface
	 */
	public constructor(canvas: HTMLCanvasElement) : this(
		raw = canvas.getCanvasSurface() ?: error("Failed creating surface for canvas $canvas")
	)

	public actual fun presentFrame() {
		raw.present()
	}

	public actual suspend fun configure(configuration: SurfaceConfiguration) {
		raw.configure(configuration)
	}

	actual override fun close() {
		raw.close()
	}
}
