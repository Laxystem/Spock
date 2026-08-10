package quest.laxla.spock.registry

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class Token(public val bits: ULong)
