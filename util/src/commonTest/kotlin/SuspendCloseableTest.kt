package quest.laxla.spock

import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SuspendCloseableTest {
	@Test
	fun `errorless execution`() = runTest {
		var closed = false
		var executed = false

		SuspendCloseable {
			closed = true
		}.use {
			executed = true
		}

		assertTrue(closed && executed)
	}

	@Test
	fun `exception in usage`() = runTest {
		var closed = false
		val closeable = SuspendCloseable {
			closed = true
		}

		val usageError = IllegalStateException("error in usage!")

		val exception = assertFails {
			closeable.use {
				throw usageError
			}
		}

		assertSame(exception, usageError)
		assertTrue(closed)
	}

	@Test
	fun `exception in closure`() = runTest {
		val closureError = IllegalStateException("error in closure!")

		val closeable = SuspendCloseable {
			throw closureError
		}

		var executed = false

		val exception = assertFails {
			closeable.use {
				executed = true
			}
		}

		assertSame(exception, closureError)
		assertTrue(executed)
	}

	@Test
	fun `exception in closure and usage`() = runTest {
		val closureError = IllegalStateException("error in closure!")
		val usageError = IllegalStateException("error in usage!")

		val closeable = SuspendCloseable {
			throw closureError
		}

		val exception = assertFails {
			closeable.use {
				throw usageError
			}
		}

		assertSame(exception, usageError)
		assertContains(exception.suppressedExceptions, closureError)
	}

	@Test
	fun `autocloseable conversion`() = runTest {
		var executed = false

		AutoCloseable {
			executed = true
		}.asSuspendCloseable().close()

		assertTrue(executed)
	}
}
