package quest.laxla.spock.toolkit

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.webgpu.Device

private val logger = KotlinLogging.logger { }

public expect suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt = 800u,
	preferredHeight: UInt = 600u,
	renderer: (Device, Surface) -> WebGpuRenderer,
)
