package quest.laxla.spock.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import quest.laxla.spock.KTarget
import quest.laxla.spock.current

public fun main(): Unit = runBlocking {
	myApplication("Spock Engine | ${KTarget.current.operatingSystem}")
}
