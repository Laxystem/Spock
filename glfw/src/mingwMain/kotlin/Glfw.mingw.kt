package quest.laxla.spock.glfw

import kotlinx.cinterop.ExperimentalForeignApi


@OptIn(ExperimentalForeignApi::class)
internal actual fun glfwTerminate() {
    glfw.glfwTerminate()
}

@OptIn(ExperimentalForeignApi::class)
public actual val GlfwTrue: Int
    get() = glfw.GL_TRUE

@OptIn(ExperimentalForeignApi::class)
public actual val GlfwFalse: Int
    get() = glfw.GL_FALSE
