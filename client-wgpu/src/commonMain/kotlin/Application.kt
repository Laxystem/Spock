package quest.laxla.spock.client.wgpu

import io.ygdrasil.webgpu.GPUDevice

public expect suspend fun webGpuApplication(
	title: String,
	preferredWidth: UInt = 800u,
	preferredHeight: UInt = 600u,
	renderer: (GPUDevice, Surface) -> WebGpuRenderer,
)
