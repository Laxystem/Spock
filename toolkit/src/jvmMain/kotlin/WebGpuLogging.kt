package quest.laxla.spock.toolkit

import ffi.globalMemory
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.ygdrasil.wgpu.*
import quest.laxla.spock.Closer


private val logger = KotlinLogging.logger { }

private val KLogger.level: Level? get() = when {
	isLoggingOff() -> Level.OFF
	isTraceEnabled() -> Level.TRACE
	isDebugEnabled() -> Level.DEBUG
	isInfoEnabled() -> Level.INFO
	isWarnEnabled() -> Level.WARN
	isErrorEnabled() -> Level.ERROR
	else -> {
		logger.warn { "Failed inferring level of logger $name" }
		null
	}
}

private fun Level.toWgpuLogLevel(): WGPULogLevel = when (this) {
	Level.OFF -> WGPULogLevel_Off
	Level.TRACE -> WGPULogLevel_Trace
	Level.DEBUG -> WGPULogLevel_Debug
	Level.INFO -> WGPULogLevel_Info
	Level.WARN -> WGPULogLevel_Warn
	Level.ERROR -> WGPULogLevel_Error
}

@Suppress("KotlinConstantConditions") // false
private fun WGPULogLevel.toKotlinLoggingLevel(): Level? = when (this) {
	WGPULogLevel_Off -> Level.OFF
	WGPULogLevel_Trace -> Level.TRACE
	WGPULogLevel_Debug -> Level.DEBUG
	WGPULogLevel_Info -> Level.INFO
	WGPULogLevel_Warn -> Level.WARN
	WGPULogLevel_Error -> Level.ERROR
	else -> {
		logger.warn { "Cannot convert wgpu log level" }
		null
	}
}

private val wgpuLogCallback by lazy {
	WGPULogCallback.allocate(globalMemory) { level, cMessage, data ->
		logger.at(level.toKotlinLoggingLevel() ?: Level.INFO) {
			message = cMessage?.toKString()
		}
	}
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
internal fun Closer.setupWgpuLogging() {
	val logLevel = logger.level ?: Level.INFO

	wgpuSetLogLevel(logLevel.toWgpuLogLevel())
	wgpuSetLogCallback(wgpuLogCallback, null)

	logger.info { "Set WebGPU log level to $logLevel" }
}
