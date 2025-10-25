package quest.laxla.spock.client.toolkit

import io.ygdrasil.webgpu.Extent3D
import quest.laxla.spock.math.Vector3ui

/**
 * @since 0.0.1-alpha.4
 * @see quest.laxla.spock.asVector3
 */
public fun Vector3ui.asSize3D(): Extent3D = Extent3D(x, y, z)
