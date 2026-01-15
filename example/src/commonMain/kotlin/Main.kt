package quest.laxla.spock.example

import io.github.oshai.kotlinlogging.Level
import quest.laxla.spock.KTarget
import quest.laxla.spock.current
import quest.laxla.spock.init.runBlockingIfSupported
import quest.laxla.spock.init.setLogLevelToIfHardcoded

internal val WindowTitle = "Spock Engine | ${KTarget.current}"

@Suppress("DEPRECATION") // WasmJS complains...
public fun main(): Unit = runBlockingIfSupported {
	setLogLevelToIfHardcoded(Level.TRACE)

	myApplication(WindowTitle)
}
