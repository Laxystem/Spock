package quest.laxla.spock.renderTest

import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.KTarget
import quest.laxla.spock.current
import quest.laxla.spock.client.toolkit.webGpuApplication

@OptIn(ExperimentalSpockApi::class)
public suspend fun renderTest(): Unit = webGpuApplication(
	"Spock Render Test | ${KTarget.current}",
	renderer = ::BasicRenderer
)
