package quest.laxla.spock.example

import quest.laxla.spock.toolkit.webGpuApplication

public suspend fun myApplication(title: String): Unit = webGpuApplication(title) { device, surface ->
	MyRenderer(device, surface)
}