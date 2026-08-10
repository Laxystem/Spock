package quest.laxla.spock.core

/**
 * Provides a [Namespace] for registration and object creation purposes.
 *
 * @since 0.0.1-alpha.4
 * @sample Property.Companion
 */
@FutureTranslation("הקשר שם")
public interface NamespaceContext {
	/**
	 * The [Namespace].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun get(): Namespace
}

/**
 * Creates a new [AbsoluteName] within this [Namespace].
 *
 * @throws IllegalArgumentException if an invalid [Name] or if it is an [AbsoluteName].
 * @since 0.0.1-alpha.4
 */
public operator fun NamespaceContext.get(name: String): AbsoluteName = RelativeName(name).inNamespace()

/**
 * Localizes the provided [RelativeName] into this [Namespace].
 *
 * @since 0.0.1-alpha.4
 */
public operator fun NamespaceContext.get(name: RelativeName): AbsoluteName = get().name.child(name.parts)

/**
 * @since 0.0.1-alpha.4
 */
public fun NamespaceContext?.orUnknown(): Namespace = this?.get() ?: Namespace.Unknown
