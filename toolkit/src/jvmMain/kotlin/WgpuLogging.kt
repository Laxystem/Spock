package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.ygdrasil.wgpu.internal.jvm.panama.WGPULogCallback
import io.ygdrasil.wgpu.internal.jvm.panama.wgpu_h
import quest.laxla.spock.Closer
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment


private val logger = KotlinLogging.logger {  }

private val KLogger.level: Level? get() = when {
	isLoggingOff() -> Level.OFF
	isTraceEnabled() -> Level.TRACE
	isDebugEnabled() -> Level.DEBUG
	isInfoEnabled() -> Level.INFO
	isWarnEnabled() -> Level.WARN
	isErrorEnabled() -> Level.ERROR
	else -> {
		logger.warn { "Cannot infer logger level" }
		null
	}
}

private fun Level.toWgpuLogLevel(): Int = when (this) {
	Level.OFF -> wgpu_h.WGPULogLevel_Off()
	Level.TRACE -> wgpu_h.WGPULogLevel_Trace()
	Level.DEBUG -> wgpu_h.WGPULogLevel_Debug()
	Level.INFO -> wgpu_h.WGPULogLevel_Info()
	Level.WARN -> wgpu_h.WGPULogLevel_Warn()
	Level.ERROR -> wgpu_h.WGPULogLevel_Error()
}

private fun convertWgpuLogLevel(level: Int): Level? = when(level) {
	wgpu_h.WGPULogLevel_Off() -> Level.OFF
	wgpu_h.WGPULogLevel_Trace() -> Level.TRACE
	wgpu_h.WGPULogLevel_Debug() -> Level.DEBUG
	wgpu_h.WGPULogLevel_Info() -> Level.INFO
	wgpu_h.WGPULogLevel_Warn() -> Level.WARN
	wgpu_h.WGPULogLevel_Error() -> Level.ERROR
	else -> {
		logger.warn { "Cannot convert wgpu log level" }
		null
	}
}

private val wgpuLogCallback by lazy {
	WGPULogCallback.allocate({ level, messageRef, data ->
		logger.at(convertWgpuLogLevel(level) ?: Level.INFO) {
			message = messageRef.getString(0)
		}
	}, Arena.global())
}

internal fun Closer.setupWgpuLogging() {
	val logLevel = logger.level ?: Level.INFO
	
	wgpu_h.wgpuSetLogLevel(logLevel.toWgpuLogLevel())
	wgpu_h.wgpuSetLogCallback(wgpuLogCallback, MemorySegment.NULL)
	
	logger.info { "Set WebGPU log level to $logLevel" }
}
