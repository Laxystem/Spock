package quest.laxla.spock.client.wgpu.renderTest

import quest.laxla.spock.init.runBlockingIfSupported

@Suppress("DEPRECATION") // WasmJS complains...
public fun main(): Unit = runBlockingIfSupported {
	renderTest()
}
