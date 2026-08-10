package quest.laxla.spock.registry.impl

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentHashMap
import kotlinx.coroutines.*
import quest.laxla.spock.ConcurrentDataStructure
import quest.laxla.spock.DelicateSpockApi
import quest.laxla.spock.core.AbsoluteName
import quest.laxla.spock.core.Namer
import quest.laxla.spock.registry.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private fun frozen(): Nothing = throw UnsupportedOperationException("Registry is Frozen, and will never be writable.")
private fun noLaunchingMemberFunctions(): Nothing =
	TODO("Kotlin does not yet support member coroutine-launching functions. See KEEP-0443")

private sealed class SubRegistry<E : Any> {
	protected abstract val map: Map<AbsoluteName, E>
	protected abstract val keysMap: Map<E, AbsoluteName>

	open operator fun get(name: AbsoluteName): E? = map[name]

	// TODO: return ... | UndecidedName, make open
	fun keyOf(resource: E): AbsoluteName? = keysMap[resource]

	operator fun contains(name: AbsoluteName): Boolean = name in map
	open operator fun contains(entry: E): Boolean = entry in keysMap

	open fun isEmpty(): Boolean = map.isEmpty()

	abstract fun toImmutable(): Immutable<E>

	class Immutable<E : Any>(
		override val map: ImmutableMap<AbsoluteName, E>,
		override val keysMap: ImmutableMap<E, AbsoluteName>
	) : SubRegistry<E>() {
		override fun toImmutable(): Immutable<E> = this
	}

	class Mutable<E : Any>(
		override val map: HashMap<AbsoluteName, E>,
		override val keysMap: HashMap<E, AbsoluteName>,
		val knownNames: HashSet<AbsoluteName>,
		val generated: Reference.Generatable<>
	) : SubRegistry<E>() {
		override fun contains(entry: E): Boolean = entry in super || entry in unnamed

		override fun toImmutable(): Immutable<E> =
			Immutable(map.toPersistentHashMap(), keysMap.toPersistentHashMap())
	}

	fun toMutable(): Mutable<E> = Mutable(HashMap(map), HashMap(keysMap))
}

private interface ReadableRegistryImpl : Registry {
	val <E : Any> ResourceClassifier<E>.subRegistry: SubRegistry<E>?
	val map: Map<ResourceClassifier<*>, SubRegistry<*>>
}

private fun <E : Any> ReadableRegistryImpl.getNow(resource: ResourceKey<E>): E? =
	resource.classifier.subRegistry?.get(resource.name)

private fun <E : Any> ReadableRegistryImpl.containsNow(resource: ResourceKey<E>): Boolean =
	resource.classifier.subRegistry?.contains(resource.name) ?: false

private fun <T : E, E : Any> ReadableRegistryImpl.keyOfNow(
	classifier: ResourceClassifier<E>,
	resource: T
): Key<T, E>? = classifier.subRegistry?.keyOf(resource)?.let { classifier.reference(it) }

private fun ReadableRegistryImpl.isEmptyNow(): Boolean = map.values.all { it.isEmpty() }

private class ImmutableRegistry(
	override val map: ImmutableMap<ResourceClassifier<*>, SubRegistry.Immutable<*>>
) : ReadableRegistryImpl {
	@Suppress("UNCHECKED_CAST")
	override val <E : Any> ResourceClassifier<E>.subRegistry: SubRegistry.Immutable<E>? get() = map[this] as SubRegistry.Immutable<E>?

	override val status: ConcurrentDataStructure.Status
		get() = ConcurrentDataStructure.Status.Frozen

	override suspend fun <E : Any> get(resource: ResourceKey<E>): E? = getNow(resource)
	override suspend fun <T : E, E : Any> keyOfOrNull(
		classifier: ResourceClassifier<E>,
		resource: T
	): Key<T, E>? = keyOfNow(classifier, resource)

	override fun contains(resource: ResourceKey<*>): Boolean = containsNow(resource)

	@DelicateSpockApi
	override suspend fun awaitReadable() {
	}

	context(coroutineScope: CoroutineScope)
	override fun <T> read(block: suspend context(Registry) () -> T): Deferred<T> = coroutineScope.async {
		block()
	}

	context(coroutineScope: CoroutineScope)
	override fun read(block: suspend context(Registry) () -> Unit): Job = coroutineScope.launch {
		block()
	}

	@DelicateSpockApi
	override suspend fun awaitReadableAndWritable() = frozen()

	context(coroutineScope: CoroutineScope)
	override fun <T> readWrite(block: suspend context(MutableRegistry) () -> T): Deferred<T> = frozen()

	context(coroutineScope: CoroutineScope)
	override fun readWrite(block: suspend context(MutableRegistry) () -> Unit): Job = frozen()

	@DelicateSpockApi
	override suspend fun awaitWritable() = frozen()

	context(coroutineScope: CoroutineScope)
	override fun <T> write(block: suspend context(Registrar) () -> T): Deferred<T> = frozen()

	context(coroutineScope: CoroutineScope)
	override fun write(block: suspend context(Registrar) () -> Unit): Job = frozen()
}

private fun generationUnsupported(suffix: String = "by this registrar"): Nothing =
	error("Dynamically generated resources unsupported $suffix")

private interface WritableRegistryImpl {
	val <E : Any> ResourceClassifier<E>.subRegistry: SubRegistry.Mutable<E>

	val map: HashMap<ResourceClassifier<*>, SubRegistry.Mutable<*>>
	fun <T : Any> CoroutineScope.generate(generator: ResourceConstructor<T>): Deferred<T>
}

private class RegistrySink(
	override val map: HashMap<ResourceClassifier<*>, SubRegistry.Mutable<*>>,
	val lookup: Registry? = Registry.Basic,
	val namer: Namer
) : WritableRegistryImpl, Registrar {

	@Suppress("UNCHECKED_CAST")
	override val <E : Any> ResourceClassifier<E>.subRegistry: SubRegistry.Mutable<E>
		get() = map.getOrPut(this) {
			SubRegistry.Mutable<E>(hashMapOf(), hashMapOf()))
		} as SubRegistry.Mutable<E>

	override fun <T : Any> CoroutineScope.generate(generator: ResourceConstructor<T>): Deferred<T> =
		if (lookup == null) generationUnsupported()
		else async {
			context(lookup) {
				generator.construct()
			}
		}

	override fun register(resource: Reference.Independent<*, *>) {
		val name = namer.generateNameFor(resource)

	}

	suspend fun build() {

	}
}

internal fun createImmutableRegistry(vararg contents: NamedDirectReference<*, *>): ImmutableRegistry {

	return
}

@OptIn(ExperimentalContracts::class)
public fun immutableRegistry(block: Registrar.() -> Unit): Registry {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
}

@OptIn(ExperimentalContracts::class)
public suspend fun Registry.deriveImmutableRegistry(block: context(Registry) Registrar.() -> Unit): Registry {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

}
