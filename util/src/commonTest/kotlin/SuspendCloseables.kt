package quest.laxla.spock

import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SuspendCloseables {
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

		assertFailsWith(IllegalStateException::class) {
			closeable.use {
				error("error!")
			}
		}

		assertTrue(closed)
	}

	@Test
	fun `exception in closure`() = runTest {
		var executed = false
		val closeable = SuspendCloseable {
			error("error in closure!")
		}

		assertFailsWith(IllegalStateException::class) {
			closeable.use {
				executed = true
			}
		}

		assertTrue(executed)
	}

	@Test
	@Suppress("VariableInitializerIsRedundant")
	fun `exception in closure and usage`() = runTest {
		val closeable = SuspendCloseable {
			error("error in closure!")
		}

		var exception: Throwable? = null

		try {
			closeable.use {
				null!!
			}
		} catch (e: Throwable) {
			exception = e
		}

		assertNotNull(exception)
		assertIs<NullPointerException>(exception)
		assertTrue(exception.suppressedExceptions.any { it is IllegalStateException })
	}
}
