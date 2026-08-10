package quest.laxla.spock.core

/**
 * Any object inherently assigned a [Name] that is thus kept through deserialization.
 *
 * @since 0.0.1-alpha.4
 */
public interface Named {
	/**
	 * The canonical name of this object using which it may be retrieved from a [Registry].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val name: AbsoluteName
}
