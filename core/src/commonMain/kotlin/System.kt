package quest.laxla.spock

import kotlin.jvm.JvmInline

/**
 * References a system registered in a [Scheduler].
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
public value class System(public val id: UInt)