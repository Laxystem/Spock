package quest.laxla.spock.client.toolkit

import io.ygdrasil.webgpu.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.w3c.dom.HTMLCanvasElement
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.vectorOf
import quest.laxla.spock.windowing.Window

// TODO: actually check what texture formats and alpha modes are supported...
private val SupportedTextureFormats = persistentSetOf(
	GPUTextureFormat.BGRA8Unorm,
	GPUTextureFormat.RGBA8Unorm,
	GPUTextureFormat.RGBA16Float
)

private val SupportedAlphaModes = persistentSetOf(
	CompositeAlphaMode.Opaque,
	CompositeAlphaMode.Premultiplied
)

private val scope = CoroutineScope(Dispatchers.Default)

private class CanvasSurfaceWindow(val surface: Surface) : Window { // todo: improve upon this cursedness
	override val shouldClose: Boolean
		get() = false

	@ExperimentalSpockApi
	override val size: Deferred<Vector2ui>
		get() = scope.async { UIntSpace.vectorOf(surface.raw.width, surface.raw.height) }

	override suspend fun close() {
		// no-op on WasmJS
	}

}

public actual class Surface(public val raw: CanvasSurface) : AutoCloseable {
	public actual val window: Window = CanvasSurfaceWindow(this)

	public actual val preferredTextureFormat: GPUTextureFormat? = raw.preferredCanvasFormat

	public actual val supportedTextureFormats: ImmutableSet<GPUTextureFormat> = SupportedTextureFormats
	public actual val supportedAlphaModes: ImmutableSet<CompositeAlphaMode> = SupportedAlphaModes

	public actual val currentTexture: SurfaceTexture get() = raw.getCurrentTexture()

	/**
	 * Create a new [quest.laxla.spock.toolkit.Surface] for this [canvas].
	 *
	 * @since 0.0.1-alpha.4
	 * @see io.ygdrasil.webgpu.HTMLCanvasElement.getCanvasSurface
	 */
	@OptIn(ExperimentalWasmJsInterop::class)
	public constructor(canvas: HTMLCanvasElement) : this(
		raw = canvas
			.unsafeCast<io.ygdrasil.webgpu.HTMLCanvasElement>()
			.getCanvasSurface()
			.let(::CanvasSurface)
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

