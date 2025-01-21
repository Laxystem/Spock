@file:OptIn(ExperimentalForeignApi::class)

package quest.laxla.spock.toolkit

import glfw.glfwGetX11Display
import glfw.glfwGetX11Window
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.size_t
import quest.laxla.spock.RawSpockApi
import quest.laxla.spock.glfw.Window

@OptIn(RawSpockApi::class)
public val Window.x11Display: CPointer<out CPointed>? get() = glfwGetX11Display(raw)

@OptIn(RawSpockApi::class)
public val Window.x11Window: size_t? get() = glfwGetX11Window(raw).takeIf { it != 0uL }
