package quest.laxla.spock.ecs

/**
 * Describes when a [System] should be scheduled to run by a [Scheduler].
 *
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public data class Schedule(
	/**
	 * The amount of ticks between executions of the [System].
	 *
	 * After the system has been executed, it won't be re-executed until [UIntRange.first] ticks have passed;
	 * And will execute after [UIntRange.last] ticks have passed since it was first scheduled or last executed.
	 *
	 * E.g. an [acceptableTickDistance] of `2u..5u` will result in a sequence of `xx___Exx___Exx___...`, where:
	 * * `x` represents a tick during which system cannot be executed;
	 * * `E` represents a tick during which the system *was* executed;
	 * * `_` represents a tick during which the system *could* have been executed by other means,
	 *   thus becoming an `E` and resetting the cycle.
	 *
	 * Values of `0u` for [UIntRange.first] and [UInt.MAX_VALUE] for [UIntRange.last] will disable the respective functionality;
	 * In other words, the former will allow the system to run in consecutive ticks, and the latter will disable scheduling.
	 *
	 * @since 0.0.1-alpha.4
	 */
	val acceptableTickDistance: UIntRange = 0u..UInt.MAX_VALUE,

	/**
	 * If not `null`, the system will be [triggered][Scheduler.trigger] whenever the provided [Compable] changes.
	 * * If it is a [Possession], the system will be triggered when the described component is set or unset,
	 *   and removed if the [Possession.possessive] or the [Possession.possessed] is deleted and not [Named].
	 * * If it is an [Entity], the system will be triggered whenever a component of that type is set or unset,
	 * and deleted if said type is deleted and not [Named].
	 *
	 * @since 0.0.1-alpha.4
	 */
	val onMutationOf: Compable? = null,

	/**
	 * Whether the [Scheduler] will [remove][Scheduler.remove] the [System] after executing it.
	 *
	 * @since 0.0.1-alpha.4
	 * @see Scheduler.persist
	 */
	val removeAfterExecution: Boolean = true,
) {
	/**
	 * @param tickDistance the amount of ticks between executions of the [System].
	 * @param removeAfterExecution whether the [Scheduler] will [remove][Scheduler.remove] the [System] after executing it.
	 * @since 0.0.1-alpha.4
	 */
	public constructor(tickDistance: UInt, removeAfterExecution: Boolean = true) : this(
		tickDistance..tickDistance,
		removeAfterExecution = removeAfterExecution
	)
}

/**
 * Creates a [Schedule].
 *
 * @since 0.0.1-alpha.4
 */
public fun Compable.onMutation(
	tickDistance: UIntRange = 0u..UInt.MAX_VALUE,
	removeAfterExecution: Boolean = true
): Schedule = Schedule(tickDistance, this, removeAfterExecution)
