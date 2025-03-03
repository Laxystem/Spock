package quest.laxla.spock

import kotlinx.coroutines.test.runTest
import kotlin.test.*

suspend fun Closer.test() {
	val closures = mutableListOf<Int>()

	assertFailsWith<UnsupportedOperationException> { +this }
	assertFailsWith<UnsupportedOperationException> { +Closer(this) }
	assertFailsWith<UnsupportedOperationException> { +Closer(Closer(this)) }
	// ad infinitum!

	this += SuspendCloseable { closures += 0 }
	this += AutoCloseable { closures += 1 }
	this += SuspendCloseable { closures += 2 }
	this += AutoCloseable { closures += 3 }
	this += AutoCloseable { closures += 4 }
	this += SuspendCloseable { closures += 5 }
	this += SuspendCloseable { closures += 6 }

	+AutoCloseable { closures += 7 }
	+SuspendCloseable { closures += 8 }
	+AutoCloseable { closures += 9 }
	+SuspendCloseable { closures += 10 }
	+SuspendCloseable { closures += 11 }
	+AutoCloseable { closures += 12 }
	+AutoCloseable { closures += 13 }

	var suspendExec = false
	val suspend = +SuspendCloseable { suspendExec = true }
	assertTrue(suspend in this)

	var autoExec = false
	+AutoCloseable { autoExec = true }

	val autoException = IllegalStateException("auto!")
	val suspendException = IllegalStateException("suspend!")

	+AutoCloseable { throw autoException }
	+SuspendCloseable { throw suspendException }

	val exception = assertFails {
		close()
	}

	assertTrue(autoExec && suspendExec)
	assertFalse(suspend in this)
	assertContains(exception.suppressedExceptions, autoException)
	assertContains(exception.suppressedExceptions, suspendException)
	assertEquals(closures, mutableListOf(13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
	assertTrue(suspendExec && autoExec)

	var closedExec = false

	assertFailsWith<IllegalStateException> {
		+SuspendCloseable { closedExec = true }
	}

	assertFalse(closedExec)
}

class CloserTest {
	@Test
	fun `empty closer constructor`() = runTest {
		Closer().test()
	}

	@Test
	fun `auto vararg closer constructor`() = runTest {
		val closures = mutableListOf<Int>()

		Closer(
			AutoCloseable { closures += 0 },
			AutoCloseable { closures += 1 },
			AutoCloseable { closures += 2 }
		).test()

		assertEquals(closures, mutableListOf(2, 1, 0))
	}

	@Test
	fun `suspend vararg closer constructor`() = runTest {
		val closures = mutableListOf<Int>()

		@Suppress("RedundantSamConstructor")
		Closer(
			SuspendCloseable { closures += 0 },
			{ closures += 1 },
			{ closures += 2 }
		).test()

		assertEquals(closures, mutableListOf(2, 1, 0))
	}
}
