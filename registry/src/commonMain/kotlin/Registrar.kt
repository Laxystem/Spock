package quest.laxla.spock.registry

import quest.laxla.spock.ConcurrentDataSink

public interface Registrar : ConcurrentDataSink<Registry, Registrar> {
	public fun register(resource: Reference.Direct)
}
