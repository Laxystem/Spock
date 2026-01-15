package quest.laxla.spock.client.wgpu.renderTest

import quest.laxla.spock.KTarget
import quest.laxla.spock.client.wgpu.webGpuApplication
import quest.laxla.spock.current

public suspend fun renderTest(): Unit = webGpuApplication(
	"Spock Render Test | ${KTarget.current}",
	renderer = ::BasicRenderer
)
