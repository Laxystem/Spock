@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.client.wgpu

import glfw.glfwGetX11Display
import glfw.glfwGetX11Window
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.size_t
import quest.laxla.spock.glfw.GlfwWindow
import quest.laxla.spock.logIfNull

private val logger = KotlinLogging.logger { }

public val x11Display: CPointer<out CPointed>?
	get() = glfwGetX11Display().logIfNull(logger) {
		"Failed acquiring X11 display handle from GLFW window"
	}

public val GlfwWindow.x11Window: size_t?
	get() = glfwGetX11Window(pointer).takeIf { it != 0uL }.logIfNull(logger) {
		"Failed acquiring X11 window handle from GLFW window"
	}
