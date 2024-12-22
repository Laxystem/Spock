package quest.laxla.spock

public interface Closer : SuspendCloseable {
	public operator fun <T> T.unaryPlus(): T where T: AutoCloseable
	public operator fun <T> T.unaryPlus(): T where T: SuspendCloseable
	
	public operator fun contains(closeable: SuspendCloseable): Boolean
}
