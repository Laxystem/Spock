package quest.laxla.spock

import kotlinx.serialization.SerializationException

/**
 * If [this] is a [SerializationException], returns it;
 * Else, wraps it within one.
 *
 * @since 0.0.1-alpha.4
 */
public fun Exception.asSerializationException(): SerializationException =
	this as? SerializationException ?: SerializationException(this)
