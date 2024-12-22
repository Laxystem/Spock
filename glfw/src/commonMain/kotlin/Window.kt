package quest.laxla.spock.glfw

import kotlinx.coroutines.Deferred
import quest.laxla.spock.SuspendCloseable

public expect class Window(width: UInt, height: UInt, title: String) : SuspendCloseable {
	public val shouldClose: Boolean
	public val size: Deferred<Pair<UInt, UInt>>
	
	override suspend fun close()
}
