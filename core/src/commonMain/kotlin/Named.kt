package quest.laxla.spock

/**
 * Any object assigned a [Name] that is thus kept through deserialization.
 *
 * @since 0.0.1-alpha.4
 */
public interface Named {
	/**
	 * The canonical name of this object through which it and only it may be retrieved from a [ResourceSource].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val name: Name
}
