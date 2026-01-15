package quest.laxla.spock

import kotlin.time.Duration


public interface Scheduler {
	/**
	 * The time an in-engine second takes to pass in the real world.
	 *
	 * Change this to increase (or decrease) the speed of the simulation.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public var engineSecond: Duration

	/**
	 * The amount of time a tick occupies in an [engineSecond].
	 *
	 * Use this value to perform calculations and for general statistics;
	 * Mutations only apply the next tick (see [nextTickLength]).
	 *
	 * @since 0.0.1-alpha.4
	 * @see ticksPerEngineSecond
	 */
	public var tickLength: EngineDuration
		@DelicateSpockApi set

	/**
	 * The amount of time a tick will occupy in an [engineSecond], *starting next tick.*
	 *
	 * @since 0.0.1-alpha.4
	 * @see Scheduler.nextTicksPerEngineSecond
	 */
	public val nextTickLength: EngineDuration

	/**
	 * Schedules a [System] to be executed.
	 *
	 * The scheduler will behave as if the system has already been executed this tick,
	 * but systems *cannot* execute on the same tick they were added.
	 *
	 * @param tangibly whether the system should be removed without being executed unless it is also [persist]ed this tick.
	 * @return a reference to [system].
	 * @since 0.0.1-alpha.4
	 * @see Schedule
	 */
	public fun schedule(
		schedule: Schedule,
		tangibly: Boolean = false,
		system: suspend context(MutableEcs) System.() -> Unit
	): System

	/**
	 * Triggers the provided [System];
	 * It'll be executed the next possible tick.
	 *
	 * Calling this function behaves the same as if the system were to be executed via [Schedule.onMutationOf].
	 *
	 * @throws NoSuchElementException if there's no such [system].
	 * @since 0.0.1-alpha.4
	 */
	@Throws(NoSuchElementException::class)
	public fun trigger(system: System)

	/**
	 * Prevents the system from being removed.
	 *
	 * This can be useful in case:
	 * * It is set to [Schedule.removeAfterExecution];
	 * * Or if it was tangibly [schedule]d.
	 *
	 * @throws NoSuchElementException if there's no such [system].
	 * @since 0.0.1-alpha.4
	 */
	@Throws(NoSuchElementException::class)
	public fun persist(system: System)

	/**
	 * Changes the provided [system]'s [schedule] and [persist]s it.
	 *
	 * The system will be scheduled as if it never had a different schedule.
	 *
	 * @throws NoSuchElementException if there's no such [system].
	 * @since 0.0.1-alpha.4
	 */
	@Throws(NoSuchElementException::class)
	public operator fun set(system: System, schedule: Schedule)

	/**
	 * Acquires the provided [system]'s [Schedule].
	 *
	 * @throws NoSuchElementException if there's no such [system].
	 * @since 0.0.1-alpha.4
	 */
	@Throws(NoSuchElementException::class)
	public operator fun get(system: System): Schedule

	/**
	 * Removes the provided [system] from this [Scheduler] - it'll never be executed again.
	 *
	 * @throws NoSuchElementException if there's no such [system].
	 * @since 0.0.1-alpha.4
	 */
	@Throws(NoSuchElementException::class)
	public fun remove(system: System)
}

/**
 * Schedules the provided [system] to be run.
 *
 * The scheduler will behave *as if* the [System] has been executed this tick.
 *
 * @since 0.0.1-alpha.4
 * @see executeImmediately
 */
context(systems: Scheduler)
public fun Schedule.execute(system: suspend context(MutableEcs) System.() -> Unit): System =
	systems.schedule(this, system = system)

/**
 * Schedules the provided [system] to be run, and runs it within the current context.
 *
 * @since 0.0.1-alpha.4
 */
context(_: Scheduler, _: MutableEcs)
public suspend fun Schedule.executeImmediately(system: suspend context(MutableEcs) System.() -> Unit): System =
	execute(system).apply { system() }

/**
 * Triggers the [this] system;
 * It'll be executed the next possible tick.
 *
 * Calling this function behaves the same as if the system were to be executed via [Schedule.onMutationOf].
 *
 * @throws NoSuchElementException if [this] system does not exist.
 * @since 0.0.1-alpha.4
 */
@Throws(NoSuchElementException::class)
context(systems: Scheduler)
public fun System.trigger() {
	systems.trigger(this)
}

/**
 * Prevents [this] system from being removed after the current execution if [Schedule.removeAfterExecution] is `true`.
 *
 * @throws NoSuchElementException if [this] system does not exist.
 * @since 0.0.1-alpha.4
 */
@Throws(NoSuchElementException::class)
context(systems: Scheduler)
public fun System.persist() {
	systems.persist(this)
}

/**
 * The [Schedule] of this system.
 *
 * @throws NoSuchElementException if [this] system does not exist.
 * @since 0.0.1-alpha.4
 */
// TODO: add @Throws(NoSuchElementException::class)
context(scheduler: Scheduler)
public var System.schedule: Schedule
	get() = scheduler[this]
	set(value) {
		scheduler[this] = value
	}

/**
 * Schedules [this] system to run again after the provided amount of ticks, and [persist]s it.
 *
 * This function is not atomic, as systems are not expected to be managed by more than one owner.
 *
 * @param removeAfterExecution whether this system should be removed after its next execution. Defaults to the current schedule's value.
 * @throws NoSuchElementException if [this] system does not exist.
 * @since 0.0.1-alpha.4
 */
@Throws(NoSuchElementException::class)
context(systems: Scheduler)
public fun System.schedule(afterTicks: UInt, removeAfterExecution: Boolean = schedule.removeAfterExecution) {
	schedule = schedule.copy(
		acceptableTickDistance = afterTicks..afterTicks,
		removeAfterExecution = removeAfterExecution
	)
}

/**
 * The amount of ticks in an [engine second][Scheduler.engineSecond].
 *
 * Mutations only apply the next tick (see [nextTicksPerEngineSecond]).
 *
 * @see Scheduler.tickLength
 * @since 0.0.1-alpha.4
 */
public var Scheduler.ticksPerEngineSecond: Double
	get() = 1.engineSeconds / tickLength
	@OptIn(DelicateSpockApi::class) set(value) {
		tickLength = 1.engineSeconds / value
	}

/**
 * The amount of ticks in an [engine second][Scheduler.engineSecond], *starting next tick.*
 *
 * @since 0.0.1-alpha.4
 * @see Scheduler.nextTickLength
 */
public val Scheduler.nextTicksPerEngineSecond: Double get() = 1.engineSeconds / tickLength
