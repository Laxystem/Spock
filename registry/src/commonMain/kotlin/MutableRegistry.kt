package quest.laxla.spock.registry

public interface MutableRegistry : Registry {
	public suspend fun <E : Any> get(resource: ResourceReferant<E>): E?
	override suspend fun <E : Any> get(resource: ResourceKey<E>): E? = get(resource as ResourceReferant<E>)


	public suspend fun <T : E, E : Any> register(resource: NonKeyReference<T, E>): Key<T, E>
	public suspend fun <T : E, E : Any> registerAndAwaitOrNull(resource: NonKeyReference<T, E>): RegistrationResult<T, E>?
	public suspend fun <T : E, E : Any> registerAndAwaitOrThrow(resource: NonKeyReference<T, E>): RegistrationResult<T, E>
}
