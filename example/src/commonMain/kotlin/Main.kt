package quest.laxla.spock.example

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import quest.laxla.spock.KTarget
import quest.laxla.spock.current
import quest.laxla.spock.init.runBlockingIfSupported

internal val WindowTitle = "Spock Engine | ${KTarget.current}"

@Suppress("DEPRECATION") // WasmJS complains...
public fun main(): Unit = runBlockingIfSupported {
	KotlinLoggingConfiguration.direct.logLevel = Level.TRACE

	myApplication(WindowTitle)
}
