package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.Size3D
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector3ui
import quest.laxla.spock.math.vectorOf

/**
 * @since 0.0.1-alpha.4
 */
public fun Size3D.asVector3() = UIntSpace.vectorOf(width, height, depthOrArrayLayers)

/**
 * @since 0.0.1-alpha.4
 */
public fun Vector3ui.asSize3D() = Size3D(x, y, z)
