package quest.laxla.spock

public interface Application : Closer {
	public suspend fun renderFrame()
}
