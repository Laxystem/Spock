package quest.laxla.spock.core

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import quest.laxla.spock.FutureValueClass

/**
 * Attempts giving arbitrary objects [Name]s.
 *
 * @since 0.0.1-alpha.4
 */
@FutureTranslation(hebrew = "שמַן")
public fun interface Namer {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun generateNameFor(obj: Any): AbsoluteName?

	/**
	 * @author Laxystem
	 */
	private data object NoopNamer : Namer {
		override fun generateNameFor(obj: Any): AbsoluteName? = null
	}

	/**
	 * @author Laxystem
	 */
	private data object InferringNamer : Namer {
		override fun generateNameFor(obj: Any): AbsoluteName? = when (obj) {
			is AbsoluteName -> obj
			is Named -> obj.name
			else -> null
		}
	}

	/**
	 * @author Laxystem
	 */
	@FutureValueClass
	private data class NamespacingNamer(val namespace: Namespace) : Namer {
		override fun generateNameFor(obj: Any): AbsoluteName? = if (obj is RelativeName) namespace[obj] else null
	}

	/**
	 * @author Laxystem
	 */
	@FutureValueClass
	private data class HashingNamer(val baseName: AbsoluteName) : Namer {
		override fun generateNameFor(obj: Any): AbsoluteName = baseName.child(Name.Part(obj.hashCode().toHexString()))
	}

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {
		/**
		 * An always-`null` returning [Namer].
		 *
		 * @since 0.0.1-alpha.4
		 */
		public fun noop(): Namer = NoopNamer

		/**
		 * Acquires names of [Named] objects, and [Name]s themselves.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public fun infer(): Namer = InferringNamer

		/**
		 * Localizes [RelativeName] objects into the provided [namespace].
		 *
		 * @since 0.0.1-alpha.4
		 */
		context(namespace: NamespaceContext)
		public fun namespace(): Namer = NamespacingNamer(namespace.get())

		/**
		 * Creates names by hashing them.
		 *
		 * @since 0.0.1-alpha.4
		 */
		context(namespace: NamespaceContext?)
		public fun hash(): Namer = HashingNamer(baseName = namespace.orUnknown()[".hashed"])
	}
}

@FutureValueClass
private data class NamerSequence(val namers: ImmutableSet<Namer>) : Namer {
	override fun generateNameFor(obj: Any): AbsoluteName? = namers.firstNotNullOfOrNull { it.generateNameFor(obj) }
}

/**
 * Creates a new [Namer] on a [firstNotNullOfOrNull]-basis.
 *
 * @since 0.0.1-alpha.4
 */
public fun Namer(namers: ImmutableSet<Namer>): Namer {
	if (namers.isEmpty()) return Namer.noop()

	val builder = persistentSetOf<Namer>().builder()

	fun ImmutableSet<Namer>.scan() {
		for (namer in this)
			if (namer is NamerSequence) scan()
			else builder += namer
	}

	namers.scan()

	return NamerSequence(builder.build())
}

/**
 * Creates a new [Namer] on a [firstNotNullOfOrNull]-basis.
 *
 * @since 0.0.1-alpha.4
 */
public fun Namer(vararg namers: Namer): Namer = Namer(persistentSetOf(*namers))

/**
 * Creates a new [Namer] on a [firstNotNullOfOrNull]-basis.
 *
 * @since 0.0.1-alpha.4
 */
@Deprecated(
	message = "Unclear notation.",
	level = DeprecationLevel.ERROR,
	replaceWith = ReplaceWith("Namer.noop()"),
)
public fun Namer(): Namer = error("unreachable")
