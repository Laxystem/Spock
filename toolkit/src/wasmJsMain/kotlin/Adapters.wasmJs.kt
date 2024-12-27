package quest.laxla.spock.toolkit

import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.internal.js.GPURequestAdapterOptions
import io.ygdrasil.wgpu.requestAdapter

public suspend fun requestAdapterOrThrow(options: GPURequestAdapterOptions? = null): Adapter =
	requestAdapter(options) ?: error("Failed acquiring WebGpu adapter")
