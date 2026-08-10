package quest.laxla.spock

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.assert as assertIntrinsic

public actual inline fun assert(value: Boolean, lazyMessage: () -> String) {
	assertIntrinsic(value) {
		runtimeAssertSuccessful(lazyMessage)
	}
}

@OptIn(ExperimentalContracts::class)
public actual inline fun assert(predicate: () -> Boolean, lazyMessage: () -> String) {
	contract {
		callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
	}

	if (BytecodeAssertions.isEnabled) assert(
		runtimeAssertSuccessful(predicate) {
			AssertionEvaluationFailedErrorText + try {
				lazyMessage().trimAndAppendIfNotBlank(prefix = ": ")
			} catch (e: Throwable) {
				if (e is SystemCaputError) {
					e.addSuppressed(it)
					throw e
				}

				it.addSuppressed(e)
				emptyString()
			}
		},
		lazyMessage
	)
}
