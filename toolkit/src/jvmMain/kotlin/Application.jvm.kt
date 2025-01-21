package quest.laxla.spock.toolkit

import ffi.LibraryLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import quest.laxla.spock.Closer

private val logger = KotlinLogging.logger {  }

internal actual suspend fun Closer.initializeWebGpu() {
	LibraryLoader.load()
	logger.debug { "Loaded WebGPU binaries" }
	
	setupWgpuLogging()
}
