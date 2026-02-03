package quest.laxla.spock.glfw

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.io.bytestring.ByteStringBuilder
import kotlinx.io.bytestring.decodeToString
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.Configuration
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicReference

private val logger = KotlinLogging.logger { }

// TODO: turn this into a standard part of engine initialization
private val lwjgl by lazy {
	if (logger.isDebugEnabled()) {
		Configuration.DEBUG.set(true)
		Configuration.DEBUG_LOADER.set(true)

		// should only be enabled locally
		// TODO: make a proper system
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true)
		Configuration.DEBUG_STACK.set(true)
		Configuration.DEBUG_FUNCTIONS.set(true)
	}

	Configuration.DEBUG_STREAM.set(PrintStream(object : OutputStream() {
		private val bytes = AtomicReference(ByteStringBuilder())

		private fun throwAlreadyClosed(): Nothing = throw IOException("Output stream already closed")

		override fun write(b: Int) = bytes.get()?.append(b.toByte()) ?: throwAlreadyClosed()

		@Synchronized
		override fun flush() {
			for (line in bytes.getAndUpdate { if (it == null) null else ByteStringBuilder() }
				?.toByteString()
				?.decodeToString()
				?.split('\r', '\n')
				?.mapNotNull {
					it
						.takeIf(String::isNotBlank)
						?.trim('\r', '\n')
				} ?: throwAlreadyClosed()
			) logger.debug { line }
		}

		override fun close() = bytes.set(null)
	}, true))
}

internal actual fun glfwInit(): Unit? = Unit.takeIf {
	lwjgl
	GLFW.glfwInit()
}

internal actual fun glfwTerminate() {
	GLFW.glfwTerminate()
}

public actual val GlfwTrue: Int get() = GLFW.GLFW_TRUE
public actual val GlfwFalse: Int get() = GLFW.GLFW_FALSE
