package quest.laxla.spock.core

import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.persistentListOf
import quest.laxla.spock.emptyString
import kotlin.jvm.JvmInline

private val forbiddenTlds = persistentHashSetOf("zip, mov")

/**
 * Prefixes a [RelativeName] into an [AbsoluteName], thus allowing easy registration of multiple names.
 *
 * By convention, namespaces should be equivalent to their associated Kotlin package, if any;
 * Even if fully code-less, they should still follow the same `tld.domain.package`.
 * If you do not own a domain, there are several options:
 * * Use your git host pages website, e.g. `page.codeberg.janedoe.foobar`, `io.github.janedoe.foobar`.
 * * Use your social media account, e.g. `social.example.janedoe.foobar` (with permission from your instance admin!).
 * * Use any reasonably-unique username using the `local` reserved TLD, e.g. `local.janedoe_abracadabra.foobar`.
 *
 * Regardless, even if you own a domain under such a TLD, **never** use:
 * * Namespaces beginning with [`engine`][Namespace.Engine], `kotlin`, `kotlinx`, `java`, `javax`,
 *   or the namespace of any other 3rd-party project -
 *   unless you're working on an **official** integration, avoid polluting others' namespaces;
 * * Namespaces similar to file extensions, e.g. the `.mov` and `.zip` TLDs
 *   (note these are considered active security threats, and thus,
 *   are actively checked for and more may be added without consideration of backwards-compatibility; simply, avoid);
 * * Namespaces beginning with [`unknown`][Namespace.Unknown] or `example` - these are reserved for runtime and documentation uses.
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
@FutureTranslation("קידומת שם")
public value class Namespace(public val name: AbsoluteName) : NamespaceContext {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public constructor(namespace: String) : this(name = AbsoluteName(namespace))

	init {
		require((name.parts.firstOrNull() ?: emptyString()) !in forbiddenTlds)
	}

	@Deprecated(
		message = "Returns self.",
		replaceWith = ReplaceWith(expression = "this"),
		level = DeprecationLevel.HIDDEN
	)
	override fun get(): Namespace = this

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {

		/**
		 * The built-in namespace.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val Engine: Namespace = Namespace("engine")

		/**
		 * @since 0.0.1-alpha.4
		 */
		public val Unknown: Namespace = Namespace("unknown")
	}
}

/**
 * The root name for [RelativeName]s.
 *
 * This root is a hard border;
 * I.e., it is impossible to access the [Name.parent] of the root name of a namespace,
 * thus making relative names completely isolated from the global namespace and perfectly namespace-leakage-safe.
 *
 * @since 0.0.1-alpha.4
 */
public val NamespaceRoot: RelativeName = RelativeName(parts = persistentListOf())
