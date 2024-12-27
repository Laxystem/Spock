@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package quest.laxla.spock.toolkit

import com.sun.jna.platform.win32.Kernel32
import darwin.CAMetalLayer
import darwin.NSWindow
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.wgpu.Adapter
import io.ygdrasil.wgpu.Surface
import io.ygdrasil.wgpu.WGPU
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.glfw.GLFWNativeWin32
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Window
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.KTarget
import quest.laxla.spock.OperatingSystem
import quest.laxla.spock.current
import quest.laxla.spock.glfw.LowLevelGlfwApi
import quest.laxla.spock.glfw.Window
import java.lang.foreign.MemorySegment

public actual typealias Wgpu = WGPU

private val logger = KotlinLogging.logger { }

internal actual fun createWgpu(): Wgpu? = WGPU.createInstance()

private const val SessionType = "XDG_SESSION_TYPE"
private const val Wayland = "wayland"
private const val X11 = "x11"

@OptIn(ExperimentalSpockApi::class)
@LowLevelGlfwApi
private fun Wgpu.getRawSurfaceOrNull(window: Window): MemorySegment? = when (KTarget.current.operatingSystem) {
	OperatingSystem.Linux, OperatingSystem.FreeBsd -> when (val sessionType = System.getenv(SessionType)) {
		X11 -> getSurfaceFromX11Window(
			glfwGetX11Display().toMemorySegment(),
			glfwGetX11Window(window.raw)
		)

		Wayland -> TODO("Wayland session support")

		else -> {
			logger.error { "Unsupported $SessionType '$sessionType'; needs to be '$X11' or '$Wayland'" }
			null
		}
	}

	OperatingSystem.Windows -> {
		getSurfaceFromWindows(
			Kernel32.INSTANCE.GetModuleHandle(null).pointer.toMemorySegment(),
			GLFWNativeWin32.glfwGetWin32Window(window.raw).toMemorySegment()
		)
	}

	OperatingSystem.MacOS -> CAMetalLayer.layer().run {
		glfwGetCocoaWindow(window.raw).rococoa<NSWindow>().contentView()?.apply {
			setWantsLayer(true)
			setLayer(id().toLong().toPointer())
		}

		getSurfaceFromMetalLayer(id().toLong().toMemorySegment())
	}

	else -> null
}?.takeUnless { it == MemorySegment.NULL }

@OptIn(LowLevelGlfwApi::class)
public actual fun Wgpu.createSurfaceOrNull(window: Window, width: UInt, height: UInt): Surface? =
	getRawSurfaceOrNull(window)?.let { raw ->
		Surface(raw) {
			width.toInt() to height.toInt()
		}
	}

public actual fun Wgpu.requestAdapterOrNull(surface: Surface): Adapter? = requestAdapter(surface)?.also { adapter ->
	surface.computeSurfaceCapabilities(adapter)
}
