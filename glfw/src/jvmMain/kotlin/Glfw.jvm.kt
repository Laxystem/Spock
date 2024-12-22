package quest.laxla.spock.glfw

import org.lwjgl.glfw.GLFW

@LowLevelGlfwApi public actual fun glfwInit(): Unit? = Unit.takeIf { GLFW.glfwInit() }
@LowLevelGlfwApi public actual fun glfwTerminate() {
	GLFW.glfwTerminate()
}

public actual val GlfwTrue: Int get() = GLFW.GLFW_TRUE
public actual val GlfwFalse: Int get() = GLFW.GLFW_FALSE
