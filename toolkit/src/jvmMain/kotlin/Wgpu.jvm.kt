@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package quest.laxla.spock.toolkit

import com.sun.jna.platform.win32.Kernel32
import darwin.CAMetalLayer
import darwin.NSWindow
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.WGPU
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandDisplay
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandWindow
import org.lwjgl.glfw.GLFWNativeWin32
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Window
import quest.laxla.spock.*
import quest.laxla.spock.glfw.GlfwWindow

public actual typealias Wgpu = WGPU

private val logger = KotlinLogging.logger { }

internal actual fun createWebGpuOrNull(): Wgpu? = WGPU.createInstance()

private const val SessionType = "XDG_SESSION_TYPE"
private const val Wayland = "wayland"
private const val X11 = "x11"

@OptIn(ExperimentalSpockApi::class)
@RawSpockApi
internal actual fun Wgpu.getRawSurfaceOrNull(window: GlfwWindow): NativeSurface? = when (KTarget.current.operatingSystem) {
	OperatingSystem.Linux, OperatingSystem.FreeBsd -> when (val sessionType = System.getenv(SessionType)) {
		X11 -> getSurfaceFromX11Window(
			glfwGetX11Display().toNativeAddress(),
			glfwGetX11Window(window.raw).toULong()
		)

		Wayland -> getSurfaceFromWaylandWindow(
			glfwGetWaylandDisplay().toNativeAddress(),
			glfwGetWaylandWindow(window.raw).toNativeAddress()
		)

		else -> {
			logger.error { "Unsupported $SessionType '$sessionType'; needs to be '$X11' or '$Wayland'" }
			null
		}
	}

	OperatingSystem.Windows -> {
		getSurfaceFromWindows(
			Kernel32.INSTANCE.GetModuleHandle(null).pointer.toNativeAddress(),
			GLFWNativeWin32.glfwGetWin32Window(window.raw).toNativeAddress()
		)
	}

	OperatingSystem.MacOS -> CAMetalLayer.layer().run {
		glfwGetCocoaWindow(window.raw).rococoa<NSWindow>().contentView()?.apply {
			setWantsLayer(true)
			setLayer(id().toLong().toPointer())
		}

		getSurfaceFromMetalLayer(id().toLong().toNativeAddress())
	}

	else -> null
}

internal actual fun Wgpu.requestAdapter(surface: NativeSurface): Adapter? = requestAdapter(nativeSurface = surface)
