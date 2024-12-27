package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import quest.laxla.spock.Closer

private val logger = KotlinLogging.logger {  }

internal actual suspend fun Closer.initializeWebGpu() {
	Wgpu.loadLibrary()
	logger.debug { "Loaded WebGPU binaries" }
	
	setupWgpuLogging()
}
