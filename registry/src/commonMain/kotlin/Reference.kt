package quest.laxla.spock.registry

import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.FutureValueClass
import quest.laxla.spock.assert
import quest.laxla.spock.core.*
import quest.laxla.spock.implicit
import quest.laxla.spock.registry.Key.Companion.SEPARATOR
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Holds a resource either via [Key], or [directly][DirectReference].
 *
 * [Named] resources acquired from [Named] references **must** have matching [Name]s.
 *
 * ```mermaid
 * ---
 * config:
 *   class:
 *     hideEmptyMembersBox: true
 * title: Reference Hierarchy
 * ---
 * classDiagram
 *     Reference: classifier
 *     Reference: toKeyOrNull() Key?
 *     Reference: toKeyOrThrow() Key
 *     Reference <|-- AsyncReference
 *     Reference <|-- Referant
 *     Reference <|-- DirectReference
 *
 *     <<interface>> Reference
 *
 *     Named: name
 *     Named <|-- Referant
 *     Named <|-- Registration
 *     <<interface>> Named
 *
 *     AsyncReference: generate()
 *     AsyncReference <|-- AsyncRegistration
 *     AsyncReference <|-- DynamicReference
 *
 *     Referant: toKey() Key
 *     Referant <|-- Key
 *     Referant <|-- DynamicReference
 *     Referant <|-- RegistryObject
 *
 *     DirectReference: get()
 *     DirectReference <|-- Registration
 *     DirectReference <|-- Box
 *
 *     Registration <|-- RegistryObject
 *     Box <|-- RegistryObject
 *
 *     <<interface>> RegistryObject
 * ```
 *
 * @param E the type of resources handled by the same [classifier], and by extension, the registry's data type.
 * @param T the type of the specific resource referenced.
 * @since 0.0.1-alpha.4
 * @see Holder
 * @see Resource
 */
public sealed interface Reference<out T : E, E : Any> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val classifier: ResourceClassifier<E>

	/**
	 * Attempts converting this Reference into a [Key], keeping only its [classifier] and [Name].
	 *
	 * @return `null` on failure
	 * @since 0.0.1-alpha.4
	 */
	public fun toKeyOrNull(): Key<T, E>? {
		val name = nameOrNull ?: return null

		return Key(classifier, name)
	}

	/**
	 * Attempts converting this Reference into a [Key], keeping only its [classifier] and [Name].
	 *
	 * @throws IllegalArgumentException on failure.
	 * @since 0.0.1-alpha.4
	 */
	public fun toKeyOrThrow(): Key<T, E> = toKeyOrNull() ?: error("Cannot infer Name of reference [$this]")
}

@Serializable
public sealed interface AcquirableReference<out T : E, E : Any> : Reference<T, E>

public typealias AcquirableResource<E> = AcquirableReference<*, E>

/**
 * Any [Reference] to a resource directly acquirable via a [MutableRegistry].
 *
 * @since 0.0.1-alpha.4
 * @see Key
 * @see DynamicReference
 */
//@Serializable(Referant.Companion::class)
public sealed interface Referant<out T : E, E : Any> : Reference<T, E>, Named, AcquirableReference<T, E> {
	/**
	 * Converts this into a [Key].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val key: Key<T, E>

	@Deprecated(
		message = "Never null.",
		level = DeprecationLevel.WARNING,
		replaceWith = ReplaceWith(expression = "this.toKey()")
	)
	override fun toKeyOrNull(): Key<T, E>? = key

	@Deprecated(
		message = "Never throws.",
		level = DeprecationLevel.WARNING,
		replaceWith = ReplaceWith(expression = "this.toKey()")
	)
	override fun toKeyOrThrow(): Key<T, E> = key

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object : KSerializer<Referant<*, *>> {
		override val descriptor: SerialDescriptor = SerialDescriptor(
			serialName = "quest.laxla.spock.registry.Referant", original = Key.descriptor
		)

		override fun serialize(encoder: Encoder, value: Referant<*, *>) {
			Key.serialize(encoder, value.key)
		}

		override fun deserialize(decoder: Decoder): Referant<*, *> = Key.deserialize(decoder)

	}
}

public typealias ResourceReferant<E> = Referant<E, E>

/**
 * References a resource of type [T] held in a registry of an unspecified compatible type.
 *
 * @since 0.0.1-alpha.4
 * @see Reference
 * @see Resource
 */
public typealias Holder<T> = Reference<T, *>

/**
 * References a resource held in a registry of [E]s.
 *
 * @since 0.0.1-alpha.4
 * @see Reference
 * @see Holder
 */
public typealias Resource<E> = Reference<E, E>

/**
 * References a resource and allows its acquirance by means other than asking a registry directly.
 *
 * Shared supertype of all [Reference]s but [Key].
 *
 * @since 0.0.1-alpha.4
 * @see Reference
 * @see NonKeyHolder
 */
public sealed interface NonKeyReference<out T : E, E : Any> : Reference<T, E>

/**
 * [Reference]s a resource of type [T] acquirable by means other than simply asking said registry for it.
 *
 * @since 0.0.1-alpha.4
 * @see Holder
 * @see NonKeyReference
 */
public typealias NonKeyHolder<T> = NonKeyReference<T, *>

/**
 * Directly holds a resource.
 *
 * This interface may be implemented by the resource itself,
 * enabling better syntax as well as some optimizations.
 *
 * Direct references **must not** hold other [Reference]s.
 *
 * @since 0.0.1-alpha.4
 * @see Reference
 * @see DirectHolder
 * @see AcquiredResource
 */
public sealed interface DirectReference<out T : E, E : Any> : NonKeyReference<T, E>, AcquirableReference<T, E> {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun get(): T

	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun toBox(): Box<T, E>

	/**
	 * @since 0.0.1-alpha.4
	 */
	@ConsistentCopyVisibility
	@FutureValueClass
	@Serializable
	public data class Packet<E : Any> private constructor(
		val resource: E,
		val classifier: ResourceClassifier<E>,
		val name: AbsoluteName? = null
	) {
		/**
		 * @since 0.0.1-alpha.4
		 */
		public constructor(resource: AcquiredResource<E>) : this(
			classifier = resource.classifier,
			resource = resource.get(),
			name = resource.nameOrNull
		)

		/**
		 * @since 0.0.1-alpha.4
		 */
		public fun toAcquiredResource(): AcquiredResource<E> =
			if (name == null) classifier.reference(resource)
			else Registration(classifier.reference(name), resource)

		/**
		 * @since 0.0.1-alpha.4
		 */
		public inline fun <reified T : E> toDirectReference(): DirectReference<T, E> {
			resource as T

			@Suppress("UNCHECKED_CAST") return toAcquiredResource() as DirectReference<T, E>
		}
	}

	public class Serializer<E : Any>(impl: KSerializer<E>) : KSerializer<AcquiredResource<E>> {
		private val impl = Packet.serializer(impl)

		override val descriptor: SerialDescriptor = SerialDescriptor(
			serialName = "quest.laxla.spock.registry.DirectReference",
			original = impl.descriptor
		)

		override fun serialize(encoder: Encoder, value: AcquiredResource<E>) {
			impl.serialize(encoder, Packet(value))
		}

		override fun deserialize(decoder: Decoder): AcquiredResource<E> = impl.deserialize(decoder).toAcquiredResource()
	}
}

/**
 * Directly-held resource of type [T].
 *
 * @since 0.0.1-alpha.4
 * @see Holder
 * @see DirectReference
 * @see AcquiredResource
 */
public typealias DirectHolder<T> = DirectReference<T, *>

/**
 * Directly-held resource that may be held in a registry of [E]s.
 *
 * @since 0.0.1-alpha.4
 * @see Resource
 * @see DirectReference
 * @see DirectHolder
 */
public typealias AcquiredResource<E> = @Serializable(DirectReference.Serializer::class) DirectReference<E, E>


public sealed interface Box<out T : E, E : Any> : DirectReference<T, E> {
	@Deprecated("Returns self", replaceWith = ReplaceWith("this"))
	override fun toBox(): Box<T, E> = this
}

public typealias ResourceBox<E> = Box<E, E>
public typealias Boxed<T> = Box<T, *>

/**
 * @author Laxystem
 */
private data class BoxedUnnamed<out T : E, E : Any>(
	override val classifier: ResourceClassifier<E>,
	private val resource: T
) : Box<T, E> {

	init {
		assert(resource !is Named) { "Resource boxed within an unnamed Box mustn't be Named." }
	}

	override fun get(): T = resource

	override fun toString(): String = "Box($resource)@${toKeylikeString()}"
}

/**
 * @since 0.0.1-alpha.4
 */
public sealed interface Registration<out T : E, E : Any> : DirectReference<T, E>, Named {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public val key: Key<T, E>

	@Deprecated(
		level = DeprecationLevel.WARNING, message = "Will never be null.", replaceWith = ReplaceWith(expression = "key")
	)
	override fun toKeyOrNull(): Key<T, E>? = key

	@Deprecated(
		level = DeprecationLevel.WARNING, message = "Will never throw.", replaceWith = ReplaceWith(expression = "key")
	)
	override fun toKeyOrThrow(): Key<T, E> = key
}

/**
 * @author Laxystem
 */
private data class BoxedNamed<out T : E, E : Any>(
	override val classifier: ResourceClassifier<E>,
	private val resource: T
) : Box<T, E>, Registration<T, E> {
	// we cannot have T be Named and E not, so...
	override val name: AbsoluteName get() = (resource as Named).name
	override val key: Key<T, E> get() = toKeyOrThrow()

	override fun get(): T = resource

	override fun toString(): String = "Box($resource)@$key"
}

private fun <T : E, E : Any> createBoxNoCheck(classifier: ResourceClassifier<E>, resource: T): Box<T, E> =
	if (resource is Named) BoxedNamed(classifier, resource)
	else BoxedUnnamed(classifier, resource)

/**
 * @author Laxystem
 */
private data class RegistrationUnnamed<out T : E, E : Any>(
	override val key: Key<T, E>,
	private val resource: T
) : Registration<T, E> {
	override val classifier: ResourceClassifier<E> get() = key.classifier
	override val name: AbsoluteName get() = key.name

	init {
		assert(resource !is Named) { "Named resource cannot be given another name." }
	}

	override fun get(): T = resource

	override fun toBox(): Box<T, E> = BoxedUnnamed(key.classifier, resource)

	override fun toString(): String = "Box($resource)@$key"

}

private fun <T : E, E : Any> createRegistrationNoCheck(
	key: Key<T, E>,
	resource: T,
): Registration<T, E> = if (resource is Named) {
	require(key.name == resource.name) { "Resource [$resource] named [${resource.name}] cannot be given name [$key]" }
	BoxedNamed(key.classifier, resource)
} else RegistrationUnnamed(key, resource)

@OptIn(ExperimentalContracts::class)
private inline fun <T : E, E : Any, reified R : DirectReference<T, E>> ResourceClassifier<E>.createDirectReference(
	resource: T,
	createReference: (ResourceClassifier<E>, T) -> R
): R {
	contract {
		callsInPlace(createReference, InvocationKind.AT_MOST_ONCE)
	}

	return when (resource) {
		is RegistryObject<*, *> -> when {
			resource.classifier !is ResourceType<*> -> error("RegistryObject [$resource] has a non-direct classifier [${resource.classifier}], which is not allowed.")
			this === resource.classifier -> @Suppress("UNCHECKED_CAST") (resource as DirectReference<T, E>)
			name == resource.classifier.name -> throw IllegalArgumentException("Referencing registry objects via non-direct classifiers is currently unsupported. Please report your use-case.")

			else -> throw IllegalArgumentException("Cannot reference foreign-classifier resource [$resource] within classifier [$this].")
		}

		is Reference<*, *> -> throw IllegalArgumentException("Cannot create a reference to a reference. Classifier [$this]; Resource [$resource].")
		else -> createReference(this, resource)
	} as R
}

public fun <T : E, E : Any> Registration(key: Key<T, E>, resource: T): Registration<T, E> =
	key.classifier.createDirectReference(resource) { _, resource ->
		createRegistrationNoCheck(key, resource)
	}

/**
 * An object bound to the [Registry] system, that can be serialized in no way but [Key].
 *
 * Registry Objects are equivalent to Minecraft's non-data-driven registries,
 * and cannot be serialized in traditional means. Thus, their usage is heavily discouraged.
 *
 * The prime example of [RegistryObject]s are [ResourceType]s.
 * To avoid the fundamental serialization limitations, most functions instead require a [ResourceClassifier],
 * supporting non-acquired [Key]s.
 *
 * @since 0.0.1-alpha.4
 */
public interface RegistryObject<out T : E, E : RegistryObject<E, E>> : Box<T, E>, Registration<T, E>, Referant<T, E> {
	override val key: Key<T, E>
		get() = classifier.reference(name)

	@Deprecated(
		level = DeprecationLevel.WARNING,
		message = "Will never be null.",
		replaceWith = ReplaceWith(expression = "key")
	)
	override fun toKeyOrNull(): Key<T, E>? = key

	@Deprecated(
		level = DeprecationLevel.WARNING,
		message = "Will never throw.",
		replaceWith = ReplaceWith(expression = "key")
	)
	override fun toKeyOrThrow(): Key<T, E> = key
}

/**
 * References a resource by means of how to construct it.
 *
 * @since 0.0.1-alpha.4
 * @see Reference
 * @see DynamicReference
 * @see AsyncRegistration
 */
public sealed interface AsyncReference<out T : E, E : Any> : NonKeyReference<T, E> {
	public val constructor: ResourceConstructor<T>
}

/**
 * @since 0.0.1-alpha.4
 * @see ResourceGenerator
 */
public val AsyncReference<*, *>.isRegistryLookupRequired: Boolean get() = constructor !is ResourceGenerator<*>

/**
 * References a resource via [key], automatically registering it if missing in a [MutableRegistry].
 *
 * Resource [constructor] result, if [Named], must have the [key]'s name.
 *
 * @see Reference
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public data class DynamicReference<out T : E, E : Any>(
	override val key: Key<T, E>,
	override val constructor: ResourceConstructor<T>
) : Referant<T, E>, AsyncReference<T, E> {
	override val classifier: ResourceClassifier<E> get() = key.classifier
	override val name: AbsoluteName get() = key.name

	public constructor(
		classifier: ResourceClassifier<E>, name: AbsoluteName, constructor: ResourceConstructor<T>
	) : this(Key(classifier, name), constructor)
}

public typealias DynamicHolder<T> = DynamicReference<T, *>
public typealias DynamicResource<E> = DynamicReference<E, E>

/**
 * Combines a resource [constructor] and a [classifier] to be registered onto.
 * May serve as a single-resource registration "packet".
 *
 * Constructed resources will be assigned names by a [Namer] if not [Named].
 *
 * @since 0.0.1-alpha.4
 */
@FutureValueClass
public data class AsyncRegistration<out T : E, E : Any>(
	override val classifier: ResourceClassifier<E>, override val constructor: ResourceConstructor<T>
) : AsyncReference<T, E>

/**
 * Attempts inferring the [Named.name] of a [Reference]d resource without consulting a [Registry].
 *
 * @since 0.0.1-alpha.4
 */
public val Holder<*>.nameOrNull: AbsoluteName?
	get() {
		val name = (this as? Named)?.name

		if (this is DirectReference) return when ((get() as? Named)?.name) {
			null -> name
			name -> name
			else -> error("Named Reference $this holds a resource with a different inherent name")
		}

		return name
	}

/**
 * Infers the [Named.name] of a [Reference]d resource without consulting a [Registry].
 *
 * @since 0.0.1-alpha.4
 */
public val Holder<Named>.name: AbsoluteName get() = nameOrNull!!

/**
 * Formats this [Resource]'s location similarly to the way a [Key] renders.
 *
 * @since 0.0.1-alpha.4
 */
public fun Resource<*>.toKeylikeString(): String {
	val classifierName = classifier.nameOrNull?.toDotFormattedStringOrSlash() ?: Name.UnknownFormatting
	val resourceName = nameOrNull?.toDotFormattedStringOrSlash() ?: Name.UnknownFormatting

	return classifierName + SEPARATOR + resourceName
}

/**
 * Creates a direct [Reference] for a [resource] of this [ResourceClassifier].
 *
 * @since 0.0.1-alpha.4
 */
public fun <T : E, E : Any> ResourceClassifier<E>.reference(resource: T): Box<T, E> =
	createDirectReference(resource, createReference = ::createBoxNoCheck)

/**
 * Creates a direct [Reference] for a [resource] of this [ResourceClassifier].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <T : E, E : Any> ResourceType<E>.reference(resource: T): Box<T, E> =
	(this as ResourceClassifier<E>).reference(resource)

/**
 * Creates a [Key] for a resource of this [ResourceClassifier] and the provided [name].
 *
 * @since 0.0.1-alpha.4
 */
public fun <T : E, E : Any> ResourceClassifier<E>.reference(name: AbsoluteName): Key<T, E> =
	Key(classifier = this, name)

/**
 * Creates a [Key] for a resource of this [ResourceClassifier] and the provided [name].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <T : E, E : Any> ResourceType<E>.reference(name: AbsoluteName): Key<T, E> =
	(this as ResourceClassifier<E>).reference(name)

/**
 * Creates a [Key] for a resource of this [ResourceClassifier] and the provided [name], localized to the namespace in context.
 *
 * @since 0.0.1-alpha.4
 */
context(_: NamespaceContext)
public infix fun <T : E, E : Any> ResourceType<E>.reference(name: Name): Key<T, E> =
	reference(name.inNamespaceIfRelative())

/**
 * Creates a [Key] for a resource of this [ResourceClassifier] and the provided [name], localized to the namespace in context.
 *
 * @since 0.0.1-alpha.4
 */
context(_: NamespaceContext)
public infix fun <T : E, E : Any> ResourceType<E>.reference(name: String): Key<T, E> =
	reference(name = RelativeName(name).inNamespace())

private val Resource<*>.missing: Nothing get() = throw NoSuchRegistryEntryException(this)

@Suppress("UnusedReceiverParameter")
@Deprecated(
	message = "Result is never null.",
	replaceWith = ReplaceWith(expression = "this.get()"),
	level = DeprecationLevel.ERROR
)
public fun DirectReference<*, *>.getOrNull(): Nothing = throw NotImplementedError("unreachable")

public suspend operator fun <E : Any> Registry.get(reference: ResourceReferant<E>): E? =
	if (this is MutableRegistry) get(reference)
	else when (reference) {
		is ResourceKey<E> -> get(reference)
		is DynamicResource<E> -> get(reference.key)
		is RegistryObject<*, *> -> {
			request(reference)
			@Suppress("UNCHECKED_CAST")
			reference as E?
		}
	}

context(_: Registry)
public suspend fun <E : Any> ResourceKey<E>.getOrNull(): E? = contextOf<Registry>()[this]

context(_: Registry)
public suspend fun <E : Any> ResourceReferant<E>.getOrNull(): E? = contextOf<Registry>()[this]

context(_: Registry)
public suspend fun <E : Any> AcquirableResource<E>.getOrNull(): E? = when (this) {
	is ResourceReferant<E> -> getOrNull()
	is AcquiredResource<E> -> get()
}

context(_: Registry)
public suspend fun <E : Any> ResourceKey<E>.get(): E = getOrNull() ?: missing

context(_: MutableRegistry)
public suspend fun <E : Any> AcquirableResource<E>.get(): E = getOrNull() ?: missing

context(_: Registry)
public suspend inline fun <reified T : Any> Key<T, in T>.getAndCast(): T = get() as T

context(_: Registry)
public suspend inline fun <reified T : Any> AcquirableReference<T, in T>.getAndCast(): T = get() as T

context(_: Registry)
public suspend inline fun <reified T : Any> Key<T, in T>.getOrNullAndCast(): T? = getOrNull() as T?

context(_: Registry)
public suspend inline fun <reified T : Any> AcquirableReference<T, in T>.getOrNullAndCast(): T? = getOrNull() as T?

context(_: Registry)
public suspend inline fun <reified T : Any> Key<T, in T>.getOrNullAndCastOrNull(): T? = getOrNull() as? T?

context(_: Registry)
public suspend inline fun <reified T : Any> AcquirableReference<T, in T>.getOrNullAndCastOrNull(): T? =
	getOrNull() as? T?

context(_: Registry)
public suspend inline fun <reified T : Any> Key<T, in T>.getOrThrowAndCastOrNull(): T? = get() as? T

context(_: Registry)
public suspend inline fun <reified T : Any> AcquirableReference<T, in T>.getOrThrowAndCastOrNull(): T? = get() as? T

context(_: Registry)
public suspend fun AcquiredResource<*>.isKeyMatchingRegistry(): Boolean = coroutineScope {
	val resource = this@isKeyMatchingRegistry
	val name = nameOrNull ?: return@coroutineScope true
	val optimalKey = classifier.reference(name)

	contextOf<Registry>().read<Boolean> {
		val registry = contextOf<Registry>()

		if (resource in registry) optimalKey.get() === resource.get()
		else optimalKey !in registry
	}.await()
}

context(_: Registry)
public suspend fun AcquiredResource<*>.validateKey() {
	val inherentName = nameOrNull ?: return
	val key = key()
	if (key.name != inherentName) throw IllegalStateException("Resource [$this] named [$inherentName] registered as [$]")
}

context(_: Registry)
public suspend fun <T : E, E : Any> DirectReference<T, E>.keyOrNull(): Key<T, E>? = contextOf<Registry>().keyOfOrNull()

context(_: Registry)
public suspend fun <T : E, E : Any> DirectReference<T, E>.key(): Key<T, E> = keyOrNull() ?: missing

context(_: Registry)
public fun request(resource: Resource<*>): Unit = implicit().request(resource)

context(_: Registrar)
public suspend fun <T : E, E : Any> register(resource: Reference.Independent<T, E>): Key<T, E> =
	registerOrNull(resource) ?: resource.missing

