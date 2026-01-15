package quest.laxla.spock.client.wgpu

import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.GPURequestAdapterOptions
import io.ygdrasil.webgpu.requestAdapter

public suspend fun requestAdapterOrThrow(options: GPURequestAdapterOptions? = null): Adapter = try {
	requestAdapter(options).getOrThrow()
} catch (e: Throwable) {
	throw RuntimeException("Failed acquiring WebGPU adapter", e)
}
