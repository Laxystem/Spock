package quest.laxla.spock

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue


class SuspendCloseables {
	@Test
	fun closure() = runTest {
		var closed = false
		var executed = false

		SuspendCloseable {
			closed = true
		}.use {
			executed = true
		}

		assertTrue(closed && executed)
	}
}
