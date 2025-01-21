package quest.laxla.spock.glfw

import org.lwjgl.glfw.GLFW
import quest.laxla.spock.RawSpockApi

@RawSpockApi public actual fun glfwInit(): Unit? = Unit.takeIf { GLFW.glfwInit() }
@RawSpockApi public actual fun glfwTerminate() {
	GLFW.glfwTerminate()
}

public actual val GlfwTrue: Int get() = GLFW.GLFW_TRUE
public actual val GlfwFalse: Int get() = GLFW.GLFW_FALSE
