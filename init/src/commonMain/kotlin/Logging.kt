package quest.laxla.spock.init

import io.github.oshai.kotlinlogging.Level

/**
 * Sets the log [Level] on targets considered "direct" by Kotlin Logging.
 *
 * Does nothing on the JVM.
 *
 * @since 0.0.1-alpha.4
 */
public expect fun setLogLevelToIfHardcoded(level: Level)
