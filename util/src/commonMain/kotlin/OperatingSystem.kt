@file:Suppress("ClassName")

package quest.laxla.spock

/**
 * An operating system supported by Spock.
 * 
 * @since 0.0.1-alpha.1
 */
public sealed interface OperatingSystem {
	/**
	 * Any operating system supported by [Kotlin/Jvm][Target.Jvm] or [Android].
	 *
	 * @since 0.0.1-alpha.1
	 */
	public sealed interface SupportedByAnyJvmBased : OperatingSystem

	/**
	 * An operating directly supported by [Kotlin/Jvm][Target.Jvm].
	 *
	 * @since 0.0.1-alpha.1
	 */
	public sealed interface JvmSupported : SupportedByAnyJvmBased

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object Android : SupportedByAnyJvmBased

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object FreeBsd : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object Linux : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object MacOS : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object Windows : JvmSupported

	/**
	 * @since 0.0.1-alpha.1
	 */
	public object iOS

	/**
	 * @since 0.0.1-alpha.1
	 */
	public companion object
}
