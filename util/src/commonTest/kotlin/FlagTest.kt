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
		assertFalse(mutex.isLocked)

		var executed = false

		flag.setWithLock(mutex) {
			assertTrue(mutex.isLocked)
			executed = true
		}

		assertTrue(executed && flag())
		assertFalse(mutex.isLocked)
	}

	@Test
	fun `set flag with mutex and owner`() = runTest {
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
		assertFalse(flag())
		assertTrue(mutex.isLocked)

		mutex.unlock(owner)

		flag.setWithLock(mutex, owner) {
			assertTrue(mutex.isLocked)
			executed = true
		}

		assertTrue(executed && flag())
		assertFalse(mutex.isLocked)
	}
}

