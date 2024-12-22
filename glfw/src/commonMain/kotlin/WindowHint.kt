package quest.laxla.spock.glfw

public expect val GlfwClientApi: Int
public expect val GlfwNoApi: Int

public expect val GlfwResizable: Int

public expect fun glfwWindowHint(hint: Int, value: Int)
