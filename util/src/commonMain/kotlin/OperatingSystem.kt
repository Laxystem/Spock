@file:Suppress("ClassName")

package quest.laxla.spock

/**
 * An operating system supported by Spock.
 * 
 * @since 0.0.1-alpha.1
 */
public sealed interface OperatingSystem {
	/**
	 * Any operating system supported by [Kotlin/Jvm][KTarget.Jvm] or [Android].
	 *
	 * @since 0.0.1-alpha.1
	 */
	public sealed interface SupportedByAnyJvmBased : OperatingSystem

	/**
	 * An operating directly supported by [Kotlin/Jvm][KTarget.Jvm].
	 *
	 * @since 0.0.1-alpha.1
	 */
	public sealed interface JvmSupported : SupportedByAnyJvmBased

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Android : SupportedByAnyJvmBased

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object FreeBsd : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Linux : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object MacOS : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Windows : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object iOS

	/**
	 * @since 0.0.1-alpha.1
	 */
	public companion object
}
