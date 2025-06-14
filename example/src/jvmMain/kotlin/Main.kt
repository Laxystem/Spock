package quest.laxla.spock.example

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
public fun main(): Unit = runBlocking {
	myApplication("Spock Engine | Jvm")
}
