package quest.laxla.spock

import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KClass

/**
 * Returns a guaranteedly-not-`null` for the class to be uniquely identified as in logs,
 * on a best-effort-basis.
 *
 * @since 0.0.1-alpha.4
 */
public val KClass<*>.fallbackName: String
	get() = qualifiedName
		?: simpleName?.let { "<unknown>.$it" }
		?: hashCode().toHexString()

@JvmSynthetic
internal expect fun Any.toIdentityStringImpl(): String

/**
 * Attempts to re-build the [String] returned by [Any.toString] if it nor [Any.hashCode] are overridden.
 * * On [KTarget.Jvm], calls the JVM's built-in `Objects.toIdentityString`.
 * * On [KTarget.Native], re-constructs the same format using [fallbackName] and `kotlin.native.identityHashCode`.
 * * On [KTarget.WasmJs], it is impossible to get an identity hash code; Thus, it only returns the [fallbackName].
 *
 * @since 0.0.1-alpha.4
 */
public fun Any?.toIdentityString(): String = this?.toIdentityStringImpl() ?: toString()
