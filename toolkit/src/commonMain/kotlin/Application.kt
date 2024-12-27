package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.Surface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import quest.laxla.spock.Closer
import quest.laxla.spock.autoclose
import kotlin.contracts.ExperimentalContracts

private val logger = KotlinLogging.logger { }

@OptIn(ExperimentalContracts::class)
public suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt = 800u,
	preferredHeight: UInt = 600u,
	renderer: (Device, Surface) -> WebGpuRenderer,
): Unit = withContext(Dispatchers.Default + CoroutineExceptionHandler { _, e ->
	logger.error(e) { "Encountered error in coroutines" }
}) {
	autoclose {
		webGpuApplication(title, preferredWidth, preferredHeight, renderer)
	}
}

internal expect suspend fun Closer.webGpuApplication(
	title: String,
	preferredWidth: UInt,
	preferredHeight: UInt,
	renderer: (Device, Surface) -> WebGpuRenderer,
)
