package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.GPUTextureFormat

/**
 * Naïvely selects a supported [GPUTextureFormat], using completely arbitrary (yet sensible) prioritization.
 *
 * @since 0.0.1-alpha.4
 */
@Suppress("FunctionName", "NonAsciiCharacters")
public fun Surface.naïvelySelectTextureFormat(): GPUTextureFormat = preferredTextureFormat
		?: GPUTextureFormat.RGBA8Unorm.takeIf { it in supportedTextureFormats }
		?: GPUTextureFormat.BGRA8Unorm.takeIf { it in supportedTextureFormats }
		?: supportedTextureFormats.first()

/**
 * @since 0.0.1-alpha.4
 */
@Deprecated("Use a double-dotted I.", replaceWith = ReplaceWith("this.naïvelySelectTextureFormat()"))
public fun Surface.naivelySelectTextureFormat(): GPUTextureFormat = naïvelySelectTextureFormat()
