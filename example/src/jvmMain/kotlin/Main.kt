package quest.laxla.spock.example

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public fun main(): Unit = runBlocking {
	withContext(newSingleThreadContext("blah")) {
		myApplication("Spock Engine | Jvm")
	}
}
