package quest.laxla.spock

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import quest.laxla.spock.ConcurrentDataStructure.Readability.IndividuallyAtomic
import quest.laxla.spock.ConcurrentDataStructure.Readability.SequentiallyAtomic
import quest.laxla.spock.ConcurrentDataStructure.Status.*
import kotlin.experimental.ExperimentalTypeInference

/**
 * Collects data to be appended into a [ConcurrentDataStructure].
 *
 * Sinks may implement [ConcurrentDataStructure] themselves,
 * as they can also be implemented simply as any regular good old builder;
 * When doing so, they must **never** be [Frozen] or [ReadLocked], and should avoid being [Busy].
 *
 * @param Base the data structure.
 * @param Sink a `Self` type.
 * @since 0.0.1-alpha.4
 */
public interface ConcurrentDataSink<Base : ConcurrentDataStructure<Base, *, Sink>, out Sink : ConcurrentDataSink<Base, Sink>>

/**
 * If this sink is a proper [ConcurrentDataStructure], acquires its status;
 * Otherwise, simply returns [ConcurrentDataStructure.Status.Sink].
 *
 * @since 0.0.1-alpha.4
 */
public val ConcurrentDataSink<*, *>.status: ConcurrentDataStructure.Status
	get() = (this as? ConcurrentDataStructure<*, *, *>)?.status ?: Sink

/**
 * An abstraction to unify implementation and syntax of concurrent data structures.
 *
 * The documentation of each function and property on the data structure should specify whether it is a read operation,
 * a write operation, both, or some unique combination thereof, as long as the distinction is clear.
 * Upon attempting a read or write operation, the function implementation should "consult" the data structure [status]
 * in order to determine whether to immediately execute the operation, suspend until doing so is possible,
 * or throw if [Status.isPermanent].
 *
 * There's no technical requirement for read and write operations to stay exclusive to their dedicated interfaces,
 * so feel free to implement more complex mechanism;
 * Just make sure to treat the data structure [status] property as the final source of truth,
 * and not its type, as for example a [Mutable] data structure always extends the [Base].
 *
 * @param Base the read-only interface of the data structure, e.g. [List]. This is a `Self` type.
 * @param Mutable the mutable interface of the data structure, e.g. [MutableList].
 * @param Sink the builder interface of the data structure, e.g. [PersistentList.Builder].
 * May extend [Base], or be extended by [Mutable].
 *
 * @since 0.0.1-alpha.4
 */
public interface ConcurrentDataStructure<Base, Mutable, out Sink>
		where Base : ConcurrentDataStructure<Base, Mutable, Sink>,
		      Mutable : Base,
		      Sink : ConcurrentDataSink<Base, Sink> {

	/**
	 * Describes data atomicity for a [ConcurrentDataStructure.Status].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public enum class Readability {
		/**
		 * Data can be read,
		 * and is guaranteed to return consistent values between calls.
		 *
		 * @since 0.0.1-alpha.4
		 */
		SequentiallyAtomic,

		/**
		 * Data can be read,
		 * but subsequent reads are not guaranteed to return consistent outputs.
		 *
		 * @since 0.0.1-alpha.4
		 */
		IndividuallyAtomic
	}

	/**
	 * Describes the status of a [ConcurrentDataStructure].
	 *
	 * @since 0.0.1-alpha.4th
	 */
	public enum class Status(
		/**
		 *
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val readability: Readability?,

		/**
		 * Can this data structure be written to, or will calls suspend until so?
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val isWritable: Boolean,

		/**
		 * If true, instead of suspending,
		 * attempts to read from this data structure while not [isReadable] or write while not [isWritable] will throw
		 * an [UnsupportedOperationException].
		 *
		 * Once a [ConcurrentDataStructure] acquires this state, its status may only become stricter, and must remain permanent;
		 * That is, a readable structure may become non-readable, and a writable registry may become non-writable,
		 * but not vice versa.
		 * In other words, a data structure of permanent status is only allowed to change its status to [Closed]
		 * (or of course, not at all).
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val isPermanent: Boolean = false
	) : Comparable<Status> {
		/**
		 * Data structure is ready for usage.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Ready(readability = IndividuallyAtomic, isWritable = true),

		/**
		 * Data structure is readable, writable, and guarantees complete thread-safety: what you write is what you get.
		 *
		 * @since 0.0.1-alpha.4
		 */
		WriteLocked(readability = SequentiallyAtomic, isWritable = true),

		/**
		 * Data structure can be written to, but not read from.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Populating(readability = null, isWritable = true),

		/**
		 * Data structure can be read from and not written to, but is not immutable,
		 * and subsequent calls might not return the same values.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Readable(readability = IndividuallyAtomic, isWritable = false),

		/**
		 * Data structure can be read from, but not written to.
		 *
		 * @since 0.0.1-alpha.4
		 */
		ReadLocked(readability = SequentiallyAtomic, isWritable = false),

		/**
		 * Data structure is currently busy and cannot be written to or read from,
		 * and calls will suspend until it is available.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Busy(readability = null, isWritable = false),

		/**
		 * Data structure is not, nor will it ever be, writable,
		 * but is not immutable nor does it guarantee consistent output,
		 * and may change via other means.
		 *
		 * From this state, the registry may only become [Closed] or [Frozen].
		 *
		 * @since 0.0.1-alpha.4
		 * @see ConcurrentDataStructure
		 */
		Source(permanently = Readable),

		/**
		 * Data structure is not, nor will it ever be, readable, and is only usable for collecting data.
		 *
		 * From this state, the registry may only become [Closed].
		 *
		 * @since 0.0.1-alpha.4
		 * @see ConcurrentDataSink
		 */
		Sink(permanently = Populating),

		/**
		 * Data structure is immutable, and thus, cannot be written to.
		 *
		 * From this state, the registry may only become [Closed].
		 *
		 * @since 0.0.1-alpha.4
		 */
		Frozen(permanently = ReadLocked),

		/**
		 * Data structure is not thread-safe, can
		 */
		Unsafe(permanently = Ready),

		/**
		 * Data structure is closed, and cannot be read from nor written to.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Closed(readability = null, isWritable = false);

		/**
		 * @param permanently The status this is the permanent version of.
		 */
		constructor(permanently: Status) : this(
			permanently.readability,
			permanently.isWritable,
			isPermanent = true
		)

		/**
		 * Can this data structure be written to, or will calls suspend until so?
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val isReadable: Boolean get() = readability != null
	}

	/**
	 * @since 0.0.1-alpha.4
	 *
	 * @see Status.isReadable
	 * @see Status.isWritable
	 * @see Status.isPermanent
	 */
	public val status: Status

	/**
	 * Suspends until the data structure is [Status.Ready].
	 *
	 * @throws UnsupportedOperationException if [Status.isPermanent] or if modification is only supported via [readWrite].
	 * @since 0.0.1-alpha.4
	 * @see readWrite
	 */
	@DelicateSpockApi
	public suspend fun awaitReadableAndWritable()

	/**
	 * Enters a [Status.WriteLocked] context.
	 * This function is re-entrant with itself, a well as [read] and [write].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [readWrite] call.
	 *
	 * @return a [Job] that'll only complete once the changes have been applied.
	 * @throws UnsupportedOperationException if [Status.isPermanent].
	 * @since 0.0.1-alpha.4
	 */
	context(coroutineScope: CoroutineScope)
	public fun readWrite(block: suspend context(Mutable) () -> Unit): Job

	/**
	 * Enters a [Status.WriteLocked] context.
	 * This function is re-entrant with itself, a well as [read] and [write].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [readWrite] call.
	 *
	 * @return the return value of the [block], [Deferred] until the changes have been applied.
	 * @throws UnsupportedOperationException if [Status.isPermanent].
	 * @since 0.0.1-alpha.4
	 */
	context(coroutineScope: CoroutineScope)
	public fun <T> readWrite(block: suspend context(Mutable) () -> T): Deferred<T>

	/**
	 * Suspends until the data structure is [Status.isReadable].
	 *
	 * @throws UnsupportedOperationException if [Status.isPermanent] or if modification is only supported via [read].
	 * @since 0.0.1-alpha.4
	 * @see readWrite
	 */
	@DelicateSpockApi
	public suspend fun awaitReadable()

	/**
	 * Enters a [SequentiallyAtomic] context.
	 * This function is re-entrant with itself, a well as [readWrite] and [write].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [read] call.
	 *
	 * @throws UnsupportedOperationException if [Status.Closed] or [Status.Sink].
	 * @since 0.0.1-alpha.4
	 * @see Status.isReadable
	 */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	context(coroutineScope: CoroutineScope)
	public fun read(block: suspend context(Base) () -> Unit): Job

	/**
	 * Enters a [SequentiallyAtomic] context.
	 * This function is re-entrant with itself, a well as [readWrite] and [write].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [read] call.
	 *
	 * @throws UnsupportedOperationException if [Status.Closed] or [Status.Sink].
	 * @since 0.0.1-alpha.4
	 * @see Status.isReadable
	 */
	@OptIn(ExperimentalTypeInference::class)
	@OverloadResolutionByLambdaReturnType
	context(coroutineScope: CoroutineScope)
	public fun <T> read(block: suspend context(Base) () -> T): Deferred<T>

	/**
	 * Suspends until the data structure is [Status.isWritable].
	 *
	 * @throws UnsupportedOperationException if [Status.isPermanent] or if modification is only supported via [write] and [readWrite].
	 * @since 0.0.1-alpha.4
	 * @see readWrite
	 */
	@DelicateSpockApi
	public suspend fun awaitWritable()

	/**
	 * Enters a [Status.isWritable] context.
	 * This function is re-entrant with itself, a well as [readWrite] and [read].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [write] call.
	 *
	 * @return a [Job] that'll only complete once the changes have been applied.
	 * @throws UnsupportedOperationException if [Status.Closed] or [Status.Frozen].
	 * @since 0.0.1-alpha.4
	 */
	context(coroutineScope: CoroutineScope)
	public fun write(block: suspend context(Sink) () -> Unit): Job

	/**
	 * Enters a [Status.isWritable] context.
	 * This function is re-entrant with itself, a well as [readWrite] and [read].
	 *
	 * The structure passed onto the [block]'s context is not guaranteed to be the same object,
	 * and its lifecycle is restricted to the [write] call.
	 *
	 * @return the return value of the [block], [Deferred] until the changes have been applied.
	 * @throws UnsupportedOperationException if [Status.Closed] or [Status.Frozen].
	 * @since 0.0.1-alpha.4
	 */
	context(coroutineScope: CoroutineScope)
	public fun <T> write(block: suspend context(Sink) () -> T): Deferred<T>
}
