package quest.laxla.spock.client

import io.ygdrasil.webgpu.GPUExtent3D
import quest.laxla.spock.math.UIntSpace
import quest.laxla.spock.math.Vector3
import quest.laxla.spock.math.vectorOf

/**
 * @since 0.0.1-alpha.4
 */
public fun GPUExtent3D.asVector3(): Vector3<UInt, UIntSpace> = UIntSpace.vectorOf(width, height, depthOrArrayLayers)
