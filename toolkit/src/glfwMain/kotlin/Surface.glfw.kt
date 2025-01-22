package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.math.component1
import quest.laxla.spock.math.component2
import quest.laxla.spock.windowing.Window

@OptIn(RawSpockApi::class)
public actual class Surface internal constructor(
	/**
	 * The Wgpu [NativeSurface] backing this [Surface].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@RawSpockApi public val raw: NativeSurface,
	public actual val window: Window
) : AutoCloseable by raw {
	public actual val preferredTextureFormat: TextureFormat? = null

	public actual val supportedTextureFormats: ImmutableSet<TextureFormat>
		get() = raw.supportedFormats.toImmutableSet()

	public actual val supportedAlphaModes: ImmutableSet<CompositeAlphaMode>
		get() = raw.supportedAlphaMode.toImmutableSet()

	public actual val currentTexture: SurfaceTexture
		get() = raw.getCurrentTexture()

	@OptIn(ExperimentalSpockApi::class)
	public actual suspend fun configure(configuration: SurfaceConfiguration) {
		val (width, height) = window.size.await()
		raw.configure(configuration, width, height)
	}

	public actual fun presentFrame() {
		raw.present()
	}
}
