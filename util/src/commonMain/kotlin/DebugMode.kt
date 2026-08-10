package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import quest.laxla.spock.DebugMode.isDebug
import quest.laxla.spock.DebugMode.tick
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Manages discovery of reasons to enable debug mode,
 * and passing that data onto [Component]s.
 *
 * The current state of the debug mode can be propagated among all components by being [tick]ed;
 * This is also done automatically whenever a new component is [register]ed.
 *
 * @since 0.0.1-alpha.4
 * @see LoggingDebugModeComponent
 */
public object DebugMode {
	private val components = mutableSetOf<Component>()
	private var isInRegistrationSaga: Boolean = false
	private val lock = reentrantLock()
	private val log = KotlinLogging.logger { }
	private val traceOverrides = hashSetOf<String>()
	private var overrides = mutableMapOf<String, Strength?>()

	/**
	 * Is debug mode currently active?
	 *
	 * Call [DebugMode.tick] to update.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public var isDebug: Boolean = false
		private set

	/**
	 * Represents how strong the debug mode is.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public enum class Strength(public val logLevel: Level) {
		/**
		 * Regular debug mode, as implied by [isDebug] being `true`.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Debug(logLevel = Level.DEBUG) {
			override fun getActualOverrideStrength(isDebugModeGloballyEnabled: Boolean): Strength = this
		},

		/**
		 * An even more expansive and detailed debug mode, that cannot be enabled globally.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Trace(logLevel = Level.TRACE) {
			override fun getActualOverrideStrength(isDebugModeGloballyEnabled: Boolean): Strength =
				if (isDebugModeGloballyEnabled) this else Debug
		};

		/**
		 * Determines the *actual* strength of [override]s assigned the current strength.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public abstract fun getActualOverrideStrength(isDebugModeGloballyEnabled: Boolean): Strength

		/**
		 * @since 0.0.1-alpha.4
		 */
		public companion object {
			/**
			 * Attempts to infer the debug mode [Strength] from the provided [logLevel].
			 *
			 * @return `null` if the provided level is not a debug mode
			 * @since 0.0.1-alpha.4
			 */
			public fun inferFromLogLevelOrNull(logLevel: Level): Strength? = entries.firstOrNull { it.logLevel == logLevel }
		}
	}

	/**
	 * Tracks the state of any functionality differing between production and debug mode.
	 *
	 * This interface's functions should be fast to execute,
	 * as they're called every [tick], a function that may be called often - for example,
	 * the Spock game engine calls it as a part of its gameplay loop.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public interface Component {
		/**
		 * Should debug mode activate if this component [isEnabled]?
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val isDefinitive: Boolean get() = false

		/**
		 * Is this component enabled?
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val isEnabled: Boolean

		/**
		 * Called whenever the [DebugMode] is [tick]ed.
		 *
		 * @return [isEnabled], thus allowing atomic state updates.
		 * @since 0.0.1-alpha.4
		 */
		@DelicateSpockApi
		public fun onDebugModeTick(isDebugModeEnabled: Boolean): Boolean = isEnabled

		/**
		 * Debug mode component that is also interested in the debug mode of specific marker strings.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public interface Hierarchal : Component {
			/**
			 * Called whenever the [DebugMode] is [tick]ed.
			 *
			 * Upon call, multiple calls to [set] will be followed,
			 * finalized by an [endDebugModeTick] call unless this function throws.
			 *
			 * @return [isEnabled], thus allowing atomic state updates.
			 * @since 0.0.1-alpha.4
			 */
			@DelicateSpockApi
			public override fun onDebugModeTick(isDebugModeEnabled: Boolean): Boolean = isEnabled


			/**
			 * Called after [onDebugModeTick] and before [endDebugModeTick] to enable or disable debug mode
			 * for the provided [name] string.
			 *
			 * @param name same as the [io.github.oshai.kotlinlogging.KLogger.name].
			 * @param strength the *exact* strength to apply.
			 * @since 0.0.1-alpha.4
			 * @see KotlinLogging.logger
			 */
			@DelicateSpockApi
			public operator fun set(name: String, strength: Strength?): Strength? =
				if (isEnabled) Strength.Debug else null

			/**
			 * Called at the end of a series of [set] calls initiated by a call to [onDebugModeTick] to end the tick for good,
			 * thus allowing atomic operation.
			 *
			 * @since 0.0.1-alpha.4
			 */
			@DelicateSpockApi
			public fun endDebugModeTick() {
			}
		}
	}

	@OptIn(DelicateSpockApi::class, ExperimentalContracts::class)
	private inline fun Component.tick(
		isDebugModeEnabled: Boolean,
		block: (Component.Hierarchal) -> Unit
	): Boolean {
		contract {
			callsInPlace(block, InvocationKind.AT_MOST_ONCE)
		}

		val value = onDebugModeTick(isDebugModeEnabled)

		if (this is Component.Hierarchal) try {
			block(this)
		} finally {
			endDebugModeTick()
		}

		return value
	}

	@OptIn(DelicateSpockApi::class)
	private fun Component.tickAndApplyOverrides(isDebugModeEnabled: Boolean) = tick(isDebugModeEnabled) {
		for (override in overrides) {
			val result = try {
				it.set(override.key, override.value)
			} catch (e: Throwable) {
				log.error(e) { "Failed toggling debug mode component ${toIdentityString()} for [${override.key}] ($this)" }
			}

			when {
				override.value == null && result != null -> log.error { "Failed disabling debug mode component ${toIdentityString()} for [${override.key}] ($this)" }
				override.value != null && result == null -> log.error { "Failed enabling debug mode component ${toIdentityString()} for [${override.key}] ($this)" }
				override.value != result -> log.warn { "Debug mode component ${toIdentityString()} was enabled on the wrong strength $result (should be ${override.value}) for [${override.key}] ($this)" }
			}
		}
	}

	private val shouldBeEnabled: Boolean get() = components.any { it.isDefinitive && it.isEnabled }

	/**
	 * Checks whether the debug mode should currently be active,
	 * and propagates this information to all [Component]s.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun tick(): Unit = lock.withLock {
		val shouldBeEnabled = shouldBeEnabled

		if (isDebug != shouldBeEnabled) {
			log.info { "Debug mode: $isDebug -> $shouldBeEnabled" }

			val strength = Strength.Trace.getActualOverrideStrength(isDebugModeGloballyEnabled = shouldBeEnabled)
			for (override in traceOverrides) overrides[override] = strength
		}

		isDebug = shouldBeEnabled

		for (component in components) {
			val isComponentEnabled = try {
				component.tickAndApplyOverrides(isDebug)
			} catch (e: Throwable) {
				log.error(e) { "Failed toggling debug mode component ${component.toIdentityString()} ($component)" }
				continue
			}

			when {
				isComponentEnabled && !isDebug -> log.warn { "Debug mode component ${component.toIdentityString()} is enabled ($component)" }
				isDebug && !isComponentEnabled -> log.error { "Debug mode component ${component.toIdentityString()} is disabled ($component)" }
			}
		}

		overrides.clear()
		log.trace { "Debug mode tick complete" }
	}

	/**
	 * Registers the provided [component] and [tick]s the debug mode if not within a [saga].
	 *
	 * @return a callback to deregister the component.
	 * @since 0.0.1-alpha.4
	 */
	public fun register(component: Component): AutoCloseable {
		lock.withLock {
			if (components.add(component)) {
				log.info { "Registered debug mode component ${component.toIdentityString()} ($component)" }

				if (!isInRegistrationSaga) tick()
			}
		}

		return AutoCloseable {
			lock.withLock {
				if (components.remove(component)) {
					log.info { "Deregistered debug mode component ${component.toIdentityString()} ($component)" }

					if (!isInRegistrationSaga) tick()
				}
			}
		}
	}

	/**
	 * Registers the provided [components] and [tick]s the debug mode if not withing a [saga].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun register(vararg components: Component): Unit = saga {
		for (component in components) register(component)
	}

	/**
	 * Registers multiple [Component]s, avoiding unnecessary duplicated [tick]ing.
	 *
	 * Sagas called within other sagas are allowed, as such scenario will still only tick once.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun saga(block: DebugMode.() -> Unit): Unit = lock.withLock {
		val isSubSaga = isInRegistrationSaga
		isInRegistrationSaga = true

		block()

		if (!isSubSaga) {
			isInRegistrationSaga = false
			tick()
		}
	}

	/**
	 * Sets the debug level of the provided [names].
	 * These are acquirable via [io.github.oshai.kotlinlogging.KLogger]s of the same name,
	 * and will propagate on [tick].
	 *
	 * @param strength determines the behaviour of the override;
	 * * [Strength.Debug] will always enable debug mode for that name.
	 * * [Strength.Debug]
	 * @since 0.0.1-alpha.4
	 */
	public fun override(vararg names: String, strength: Strength?): Unit = lock.withLock {
		for (name in names) overrides[name] = strength

		if (strength == Strength.Trace) traceOverrides.addAll(names)
		else traceOverrides.removeAll(names.toSet())
	}

	init {
		saga {
			register(LoggingDebugModeComponent)
			onInit()
		}
	}

	override fun toString(): String = "DebugMode($isDebug)"
}

internal expect fun DebugMode.onInit()
