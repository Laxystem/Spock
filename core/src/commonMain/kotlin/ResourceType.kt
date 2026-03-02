package quest.laxla.spock

public interface ResourceType<T : Any> {
	public infix fun accepts(resource: T)
}