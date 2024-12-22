package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {  }

internal actual suspend fun Closer.afterGlfwInit() {
	Wgpu.loadLibrary()
	logger.debug { "Loaded WebGPU binaries" }
	
	setupWgpuLogging()
}
