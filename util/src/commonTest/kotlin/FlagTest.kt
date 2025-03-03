package quest.laxla.spock

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class FlagTest {
	@Test
	fun `set flag`() {
		val flag = Flag()
		assertFalse(flag())
		flag.set()
		assertTrue(flag())
		flag.set()
		assertTrue(flag())
	}

	@Test
	fun `set flag with block`() {
		val flag = Flag()
		assertFalse(flag())

		var executed = false

		flag.set {
			executed = true
		}

		assertTrue(flag() && executed)
	}

	@Test
	fun `set flag with exception`() {
		val flag = Flag()
		assertFalse(flag())

		val exception = IllegalStateException("error!")

		val e = assertFails {
			flag.set {
				throw exception
			}
		}

		assertSame(exception, e)
		assertFalse(flag())
	}

	@Test
	fun `set flag with mutex`() = runTest {
		val flag = Flag()
		val mutex = Mutex()
		assertFalse(flag())

		var executed = false

		flag.setWithLock(mutex) {
			executed = true
		}

		assertTrue(executed && flag())
	}

	@Test
	fun `set flag with mutex locks`() = runTest {
		val flag = Flag()
		val mutex = Mutex()
		assertFalse(flag())
		assertFalse(mutex.isLocked)

		val owner = "the people"
		mutex.lock(owner)

		var executed = false
		assertFailsWith<IllegalStateException> {
			flag.setWithLock(mutex, owner) {
				executed = true
			}
		}

		assertFalse(executed)
	}
}

