package quest.laxla.spock

import kotlinx.serialization.Serializable

/**
 * Any object that may carry components, that is, an [Entity] or a [Possession].
 *
 * @since 0.0.1-alpha.4
 * @see Ecs
 */
// TODO: make EcsDsl KT-83100
@Serializable
public sealed interface Compable
