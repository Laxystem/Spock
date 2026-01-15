package quest.laxla.spock.init

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level

public actual fun setLogLevelToIfHardcoded(level: Level) {
	KotlinLoggingConfiguration.logLevel = level
}