package quest.laxla.spock.toolkit

import ffi.NativeAddress
import glfw.glfwGetWin32Window
import io.ygdrasil.webgpu.NativeSurface
import kotlinx.cinterop.*
import platform.windows.GetModuleHandle
import quest.laxla.spock.glfw.GlfwWindow

@ExperimentalForeignApi
private fun NativePtr.asCOpaque() = interpretCPointer<COpaque>(this)

@OptIn(ExperimentalForeignApi::class)
internal actual fun Wgpu.getRawSurfaceOrNull(window: GlfwWindow): NativeSurface? {
	val window = glfwGetWin32Window(window.pointer) ?: return null
	val moduleHandle: COpaquePointer = GetModuleHandle?.invoke(null)?.rawValue?.asCOpaque()?.reinterpret() ?: return null

	return getSurfaceFromWindows(NativeAddress(moduleHandle), NativeAddress(window))
}
