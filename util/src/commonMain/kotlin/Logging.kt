package quest.laxla.spock

import io.github.oshai.kotlinlogging.*
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.contract

@PublishedApi
internal val fallbackLog: KLogger = KotlinLogging.logger { }

/**
 * An error whose logging will likely fail,
 * and should preferably cause the process to crash.
 *
 * When (and hopefully if) you catch this error,
 * you're allowed to break anything you want as long as that won't cause the crash handler to die too;
 * That being said, it is better not to break things, as to decrease the chance of accidental homicide.
 *
 * This is essentially a weakened form of a [kotlin.system.exitProcess], and a strengthened form of an [AssertionError].
 *
 * @since 0.0.1-alpha.4
 */
public open class SystemCaputError(
	message: String,
	cause: Throwable? = null
) : AssertionError(message, cause), AssertionStrength.Specified {
	override val assertionStrength: AssertionStrength
		get() = AssertionStrength.Capitulation
}

/**
 * Only executes [message] if [this] is `null`.
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class, ExperimentalExtendedContracts::class)
public inline fun <T> T.logIfNull(
	logger: KLogger,
	at: Level = Level.ERROR,
	marker: Marker? = null,
	crossinline message: () -> String
): T {
	contract {
		(this@logIfNull == null) holdsIn message
	}

	if (this == null) logger.at(at, marker) {
		this.message = message()
	}

	return this
}

/**
 *
 */
public inline fun <T : Throwable> T.alsoLog(
	logger: KLogger,
	at: Level = Level.WARN,
	marker: Marker? = null,
	crossinline message: () -> String? = { null }
): T = try {
	logger.at(level = at, marker) {
		cause = this@alsoLog
		this.message = message()
	}

	return this
} catch (e: Throwable) {
	addSuppressed(e)
	if (this is SystemCaputError) return this
	if (e is SystemCaputError) throw e

	try {
		fallbackLog.error(e) { "Failed logging exception to log!" }

		return this
	} catch (e2: Throwable) {
		if (e2 is SystemCaputError) throw e2

		val error = SystemCaputError("Failed logging the failed logging of an exception to log!", e2)
		error.addSuppressed(this)
		error.addSuppressed(e)
		try {
			error.printStackTrace()
		} catch (e3: Throwable) {
			error.addSuppressed(e3)
		}
		throw error
	}
}

// TODO: expose registered log level management as a standalone API, instead of just a debug mode component?
/**
 * Manages [KLogger]s' log levels for the [LoggingDebugModeComponent].
 *
 * May be registered via [LoggingDebugModeComponent.registerLogLevelManager],
 * or implemented directly by [KLoggerFactory]s.
 *
 * @since 0.0.1-alpha.4
 * @see Provider
 */
public interface LogLevelManager {
	/**
	 * The current, top-level log level.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public var logLevel: Level

	/**
	 * Detects the log level as if the [KLoggerFactory] just initialized.
	 *
	 * @return `null` if unsupported.
	 * @since 0.0.1-alpha.4
	 */
	public fun detectLogLevel(): Level? = null

	/**
	 * Attempts mapping a [LogLevelManager] to a provided [KLoggerFactory], returning `null` on failure.
	 *
	 * Providers are allowed to construct new [LogLevelManager]s, and to store the provided logger factory within them;
	 * [LogLevelManager]s are associated one-to-one with a factory.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public typealias Provider<T> = (T) -> LogLevelManager?

	/**
	 * Acquires and sets the generic log level for the [DirectLoggerFactory].
	 *
	 * Automatically registered to the [LoggingDebugModeComponent], no need to do so manually.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public data object Direct : LogLevelManager {
		override var logLevel: Level by KotlinLoggingConfiguration.direct::logLevel

		@Suppress("RedundantNullableReturnType")
		override fun detectLogLevel(): Level? = Level.INFO // hardcoded
	}

	/**
	 * Supports setting log levels to specific [KLogger]s.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public interface Hierarchal : LogLevelManager {
		/**
		 * The log level of a logger [named][KLogger.name] as provided.
		 *
		 * The function should return the log level set specifically for this logger;
		 * If none is set and supported, it should instead defer to its (dot-separated) parent's log level;
		 * As a last resort, defer to the root [logLevel].
		 *
		 * @since 0.0.1-alpha.4
		 */
		public fun logLevelOf(name: String): Level

		/**
		 * Sets the log [level] of for the provided [KLogger.name];
		 * a `null` value will remove the logger's log level override,
		 * thus deferring as described by [logLevelOf].
		 *
		 * When reset via a `null` value, this function should attempt and preserve the behaviour found
		 * before the intervention of the debug mode mechanism.
		 *
		 * @return [logLevelOf], or `null` if the log was set to or unsuccessfully set from a state where it inherited its effective log level.
		 * @since 0.0.1-alpha.4
		 */
		public fun setLogLevelOf(name: String, level: Level?): Level?

		/**
		 * Detects the log level that would be assigned to a logger of the provided [KLogger.name] by default.
		 *
		 * @return `null` if unsupported or if the logger would by default inherit its parent's level.
		 * @since 0.0.1-alpha.4
		 */
		public fun detectLogLevelOf(name: String): Level? = null
	}
}

/**
 * Manages log [Level]s to comply with the current [DebugMode].
 *
 * Uses [LogLevelManager]s to manage the log level set to the current [KLoggerFactory].
 *
 * @since 0.0.1-alpha.4
 * @see registerLogLevelManager
 */
public data object LoggingDebugModeComponent : DebugMode.Component.Hierarchal {
	private val lock = reentrantLock()
	private val logLevelManagers = mutableListOf<LogLevelManager.Provider<KLoggerFactory>>()

	/**
	 * Registers a [LogLevelManager.Provider] to be called to acquire a [LogLevelManager].
	 *
	 * The registered provider will be called only after all already-registered providers have been checked and failed to provide
	 * a manager.
	 *
	 * @return a callback to deregister the provider.
	 * @since 0.0.1-alpha.4
	 */
	public fun registerLogLevelManager(provider: LogLevelManager.Provider<KLoggerFactory>): AutoCloseable {
		lock.withLock { logLevelManagers += provider }

		return AutoCloseable {
			lock.withLock {
				logLevelManagers -= provider
			}
		}
	}

	init {
		registerLogLevelManager<DirectLoggerFactory>(LogLevelManager.Direct)
		registerLogLevelManager<LogLevelManager> { it }
		onInit(managers = this)
	}

	private var latestLoggerFactory: KLoggerFactory? = null
	private var logLevelManager: LogLevelManager? = null
		get() = lock.withLock {
			val loggerFactory = KotlinLoggingConfiguration.loggerFactory

			if (latestLoggerFactory !== loggerFactory) {
				latestLoggerFactory = loggerFactory
				field = logLevelManagers.firstNotNullOfOrNull { it(loggerFactory) }

				if (field == null) fallbackLog.error {
					"Could not acquire a LogLevelManager for KLoggerFactory ${loggerFactory.toIdentityString()}; " +
							"Register one via quest.laxla.spock.LoggingDebugMode.set(...), " +
							"else debug mode logging integration won't work ($loggerFactory)"
				}
			}

			field
		}

	override var isEnabled: Boolean
		get() = logLevelManager?.let { it.logLevel <= Level.DEBUG } == true
		set(value) = lock.withLock {
			val isCurrentlyEnabled = isEnabled

			when {
				isCurrentlyEnabled && !value -> logLevelManager?.run { logLevel = detectLogLevel() ?: Level.INFO }
				!isCurrentlyEnabled && value -> logLevelManager?.run { logLevel = Level.DEBUG }
			}
		}

	@DelicateSpockApi
	override fun onDebugModeTick(isDebugModeEnabled: Boolean): Boolean {
		lock.lock()
		isEnabled = isDebugModeEnabled
		return isEnabled
	}

	private val hierarchalLogLevelManager: LogLevelManager.Hierarchal? get() = logLevelManager as? LogLevelManager.Hierarchal?

	@DelicateSpockApi
	override fun set(name: String, strength: DebugMode.Strength?): DebugMode.Strength? =
		hierarchalLogLevelManager?.setLogLevelOf(name, strength?.logLevel)
			?.let(DebugMode.Strength::inferFromLogLevelOrNull)

	@DelicateSpockApi
	override fun endDebugModeTick() {
		lock.unlock()
	}
}

internal expect fun LoggingDebugModeComponent.onInit(): DebugMode.Component

/**
 * Registers a [LogLevelManager.Provider] to be called to acquire a [LogLevelManager] for [KLoggerFactory]s of type [T].
 *
 * The registered provider will be called only after all already-registered providers have been checked and failed to provide
 * a manager.
 *
 * @return a callback to deregister the provider.
 * @since 0.0.1-alpha.4
 */
public inline fun <reified T> LoggingDebugModeComponent.registerLogLevelManager(crossinline provider: (T) -> LogLevelManager?): AutoCloseable =
	registerLogLevelManager {
		if (it is T) provider(it)
		else null
	}

/**
 * Registers a [LogLevelManager] to be used for [KLoggerFactory]s of type [T].
 *
 * The registered provider will be called only after all already-registered providers have been checked and failed to provide
 * a manager.
 *
 * @return a callback to deregister the provider.
 * @since 0.0.1-alpha.4
 */
public inline fun <reified T> LoggingDebugModeComponent.registerLogLevelManager(logLevelManager: LogLevelManager): AutoCloseable =
	registerLogLevelManager<T> { logLevelManager }
