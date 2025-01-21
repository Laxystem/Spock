@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.WGPU
import kotlinx.cinterop.ExperimentalForeignApi

public actual typealias Wgpu = WGPU

internal actual fun createWebGpuOrNull(): Wgpu? = WGPU.createInstance()

public actual fun Wgpu.requestAdapter(surface: NativeSurface): Adapter? = requestAdapter(nativeSurface = surface)
