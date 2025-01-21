package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.internal.js.GPURequestAdapterOptions
import io.ygdrasil.webgpu.requestAdapter

public suspend fun requestAdapterOrThrow(options: GPURequestAdapterOptions? = null): Adapter =
	requestAdapter(options) ?: error("Failed acquiring WebGpu adapter")
