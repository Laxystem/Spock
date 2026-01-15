package quest.laxla.spock.client.wgpu

import io.ygdrasil.webgpu.GPUTextureFormat

/**
 * Naively selects a supported [GPUTextureFormat], using completely arbitrary (yet sensible) prioritization.
 *
 * @since 0.0.1-alpha.4
 */
public fun Surface.naivelySelectTextureFormat(): GPUTextureFormat = preferredTextureFormat
		?: GPUTextureFormat.RGBA8Unorm.takeIf { it in supportedTextureFormats }
		?: GPUTextureFormat.BGRA8Unorm.takeIf { it in supportedTextureFormats }
		?: supportedTextureFormats.first()
