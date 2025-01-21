package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Deferred
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.glfw.Window
import quest.laxla.spock.math.Vector2ui
import quest.laxla.spock.math.component1
import quest.laxla.spock.math.component2

@OptIn(RawSpockApi::class)
public actual class Surface internal constructor(
	/**
	 * The Wgpu [NativeSurface] backing this [Surface].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@RawSpockApi public val raw: NativeSurface,
	/**
	 * The window onto which this [Surface] is drawn.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val window: Window
) : AutoCloseable by raw {
	@OptIn(ExperimentalSpockApi::class)
	public actual val size: Deferred<Vector2ui> get() = window.size

	public actual val preferredTextureFormat: TextureFormat? = null

	public actual val supportedTextureFormats: ImmutableSet<TextureFormat>
		get() = raw.supportedFormats.toImmutableSet()

	public actual val supportedAlphaModes: ImmutableSet<CompositeAlphaMode>
		get() = raw.supportedAlphaMode.toImmutableSet()

	public actual val currentTexture: SurfaceTexture
		get() = raw.getCurrentTexture()

	public actual suspend fun configure(configuration: SurfaceConfiguration) {
		val (width, height) = size.await()
		raw.configure(configuration, width, height)
	}

	public actual fun presentFrame() {
		raw.present()
	}
}
