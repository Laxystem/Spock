package quest.laxla.spock.renderTest

import quest.laxla.spock.init.runBlockingIfSupported

@Suppress("DEPRECATION") // WasmJS complains...
public fun main(): Unit = runBlockingIfSupported {
	renderTest()
}
