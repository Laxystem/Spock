@file:Suppress("ClassName")

package quest.laxla.spock

/**
 * An operating system supported by Spock.
 * 
 * @since 0.0.1-alpha.1
 */
public interface OperatingSystem {
	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Android : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object FreeBsd : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Linux : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object MacOS : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Windows : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object iOS : OperatingSystem

	/**
	 * @since 0.0.1-alpha.4
	 */
	public data object iPadOS : OperatingSystem

	/**
	 * @since 0.0.1-alpha.1
	 */
	public companion object
}
