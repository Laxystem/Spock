package quest.laxla.spock.example

import kotlinx.coroutines.runBlocking
import quest.laxla.spock.KTarget
import quest.laxla.spock.current

public fun main(): Unit = runBlocking {
	myApplication("Spock Engine | ${KTarget.current.operatingSystem}")
}
