@file:Suppress("ClassName")

package quest.laxla.spock

/**
 * An operating system supported by Spock.
 * 
 * @since 0.0.1-alpha.1
 */
public interface OperatingSystem {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public interface Unix : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Android : Unix

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object FreeBsd : Unix

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Linux : Unix

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object MacOS : Unix

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Windows : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object iOS : Unix

	/**
	 * @since 0.0.1-alpha.4
	 */
	public data object iPadOS : Unix

	/**
	 * @since 0.0.1-alpha.1
	 */
	public companion object
}
