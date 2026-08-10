package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private val log = KotlinLogging.logger { }

/**
 * Explains the expected behaviour of an assertion.
 *
 * Non-[RuntimeException]s, non-[Error]s (that is, regular, Java-checked [Exception]s)
 * are [asserted][Assertion] never to arrive to a global exception handler to begin with.
 *
 * @since 0.0.1-alpha.4
 */
public enum class AssertionStrength(
	/**
	 * If ran within the same process as launcher or a dev-env, should it be considered corrupted as well?
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val isViral: Boolean = false,
) {
	/**
	 * Regular Kotlin [require]-style check.
	 *
	 * Throws a [RuntimeException] on failure, is error-handled per usual.
	 *
	 * @since 0.0.1-alpha.4
	 * @see require
	 * @see check
	 * @see error
	 */
	Requirement,

	/**
	 * Non-recoverable error meant to prompt the editor or the active debugger if available,
	 * otherwise gracefully crashing the game.
	 *
	 * Throws any of the following:
	 * * [ConcurrentModificationException];
	 * * [NotImplementedError];
	 * * [WrongTargetError];
	 * * [AssertionError];
	 *
	 * @since 0.0.1-alpha.4
	 * @see assert
	 */
	Assertion,

	/**
	 * An [OutOfMemoryError].
	 *
	 * The program is expected to attempt consuming the lowest amount of memory possible while trying to,
	 * in this order of priorities:
	 * 1. Avoid the corruption of user data.
	 * 2. Allow the crash handler to behave, thus reporting the error.
	 * 3. Back up the current world state, if there is one.
	 * 4. Free non-critical memory to allow the program to continue to run.
	 *
	 * Three [OutOfMemoryError]s in the same session should escalate into a [Capitulation].
	 *
	 * @since 0.0.1-alpha.4
	 */
	OutOfMemory(isViral = true),

	/**
	 * Dev-env-breaking error.
	 *
	 * Throws a [SystemCaputError].
	 * The program will attempt to back up user data,
	 * avoiding overriding the existing file if possible,
	 * and handle the crash as carefully as it manages to,
	 * then kill the process, all while dumping the most debug data possible.
	 *
	 * @since 0.0.1-alpha.4
	 */
	Capitulation(isViral = true);

	/**
	 * [Throwable]s may implement this interface to increase their [Throwable.assertionStrength].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public interface Specified {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public val assertionStrength: AssertionStrength
	}
}

private val Throwable.stdlibAssertionStrength: AssertionStrength?
	get() = when (this) {
		is OutOfMemoryError -> AssertionStrength.OutOfMemory
		is NotImplementedError, is AssertionError, is ConcurrentModificationException -> AssertionStrength.Assertion
		is RuntimeException -> AssertionStrength.Requirement
		else -> null
	}

/**
 * Infers the [AssertionStrength] of this [Throwable].
 *
 * Formerly-`null` return values becoming non-`null` is not considered a breaking change.
 *
 * @since 0.0.1-alpha.4
 */
public val Throwable.assertionStrength: AssertionStrength?
	get() {
		val standard = stdlibAssertionStrength
		val custom = (this as? AssertionStrength.Specified)?.assertionStrength

		return when {
			standard == null && custom == null -> if (this is Error) AssertionStrength.Assertion else null

			standard == null -> custom
			custom == null -> standard

			else -> maxOf(standard, custom)
		}
	}

/**
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
@PublishedApi
internal inline fun <T> runtimeAssertSuccessful(
	block: () -> T,
	message: (Throwable) -> String = { "Assertion message composition failed" }
): T {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		callsInPlace(message, InvocationKind.AT_MOST_ONCE)
	}

	return try {
		block()
	} catch (e: Throwable) {
		if (e is AssertionError) throw e
		throw AssertionError(message(e), e)
	}
}

/**
 * @since 0.0.1-alpha.4
 */
@PublishedApi
internal const val AssertionFailedErrorText: String = "Assertion failed"

/**
 * @since 0.0.1-alpha.4
 */
@PublishedApi
internal const val AssertionEvaluationFailedErrorText: String = "Assertion evaluation failed"

/**
 * Throws an [AssertionError] if [value] is `false` if assertions have been enabled.
 *
 * @since 0.0.1-alpha.4
 * @see check
 */
public expect inline fun assert(value: Boolean, lazyMessage: () -> String = { AssertionFailedErrorText })

/**
 * Throws an [AssertionError] if this [predicate] is `false` if assertions have been enabled.
 *
 * @since 0.0.1-alpha.4
 */
public expect inline fun assert(predicate: () -> Boolean, lazyMessage: () -> String)

/**
 * Throws an [AssertionError] if this [predicate] is false if assertions have bene enabled.
 *
 * @since 0.0.1-alpha.4
 */
public inline fun assert(message: String = AssertionFailedErrorText, predicate: () -> Boolean) {
	assert(predicate) { message }
}

/**
 * Throws an [AssertionError] if this [predicate] is `false` if assertions have been enabled.
 *
 * @since 0.0.1-alpha.4
 */
public inline fun <T> T.assert(
	predicate: (T) -> Boolean,
	lazyMessage: T.() -> String
): T = apply {
	assert(
		predicate = { -> predicate(this) },
		lazyMessage = { lazyMessage() }
	)
}

/**
 * Throws an [AssertionError] if this [predicate] is `false` if assertions have been enabled.
 *
 * @since 0.0.1-alpha.4
 */
public inline fun <T> T.assert(
	message: String = emptyString(),
	predicate: (T) -> Boolean
): T = assert(
	predicate,
	lazyMessage = { message }
)
