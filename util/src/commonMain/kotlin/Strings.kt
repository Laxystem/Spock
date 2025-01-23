package quest.laxla.spock

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns an empty [String], `""`.
 *
 * @since 0.0.1-alpha.4
 */
public fun emptyString(): String = ""

/**
 * If `null`, returns an empty [String];
 * otherwise, returns the result of [block].
 *
 * @since 0.0.1-alpha.4
 * @see String.orEmpty
 */
@OptIn(ExperimentalContracts::class)
public inline fun String?.letOrEmpty(block: (String) -> String): String {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}

	return if (isNullOrBlank()) emptyString() else block(this)
}
