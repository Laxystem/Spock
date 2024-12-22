package quest.laxla.spock.glfw

import org.lwjgl.glfw.GLFW

public actual fun glfwWindowHint(hint: Int, value: Int) {
	GLFW.glfwWindowHint(hint, value)
}

public actual val GlfwClientApi: Int get() = GLFW.GLFW_CLIENT_API
public actual val GlfwNoApi: Int get() = GLFW.GLFW_NO_API

public actual val GlfwResizable: Int get() = GLFW.GLFW_RESIZABLE
