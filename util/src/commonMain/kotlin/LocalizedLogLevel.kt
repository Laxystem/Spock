package quest.laxla.spock

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level

/**
 * Marks classes that support changing their own log level arbitrarily and independently of the global log level.
 *
 * @since 0.0.1-alpha.4
 */
public interface LocalizedLogLevel {
	/**
	 * The log level this object restricts itself to.
	 *
	 * No synchronization is required to change this property, as it is essentially stateless.
	 * Implementations are expected to use its *current* state and never cache it.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public var subjectiveLogLevel: Level
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see KLogger.debug
 * @see LocalizedLogLevel
 */
@FutureContextFunction
public inline fun <T : LocalizedLogLevel> T.debugVia(
	logger: KLogger,
	throwable: Throwable? = null,
	crossinline message: T.() -> Any?
) {
	if (subjectiveLogLevel <= Level.DEBUG) logger.debug(throwable) { message() }
}

/**
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see KLogger.trace
 * @see LocalizedLogLevel
 */
@FutureContextFunction
public inline fun <T : LocalizedLogLevel> T.traceVia(
	logger: KLogger,
	throwable: Throwable? = null,
	crossinline message: T.() -> Any?
) {
	if (subjectiveLogLevel <= Level.TRACE) logger.trace(throwable) { message() }
}
