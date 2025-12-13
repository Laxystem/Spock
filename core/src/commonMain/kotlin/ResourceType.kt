package quest.laxla.spock


import kotlinx.coroutines.flow.Flow

// TODO: remember what this does
public fun interface ResourceType<T> where T : Any {
	public suspend fun Flow<T>.collapse(): T?
}
