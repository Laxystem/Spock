package quest.laxla.spock

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.ExperimentalExtendedContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KClass

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
