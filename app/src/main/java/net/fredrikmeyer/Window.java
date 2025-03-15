package net.fredrikmeyer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

/**
 * Manages the GLFW window and OpenGL context.
 */
public class Window {
    private long windowHandle;
    private int width;
    private int height;
    private String title;
    private boolean resizable;

    /**
     * Creates a new Window with the specified dimensions and title.
     *
     * @param width     the width of the window
     * @param height    the height of the window
     * @param title     the title of the window
     * @param resizable whether the window is resizable
     */
    public Window(int width, int height, String title, boolean resizable) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.resizable = resizable;
    }

    /**
     * Initializes GLFW and creates the window.
     */
    public void init() {
        // Set up an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Center the window on the screen
        centerWindow();

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);

        // Initialize OpenGL capabilities
        GL.createCapabilities();
    }

    /**
     * Centers the window on the screen.
     */
    private void centerWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            // Get the window size
            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }

    /**
     * Sets a key callback for the window.
     *
     * @param callback the key callback
     */
    public void setKeyCallback(org.lwjgl.glfw.GLFWKeyCallback callback) {
        glfwSetKeyCallback(windowHandle, callback);
    }

    /**
     * Sets a key callback for the window using a lambda expression.
     *
     * @param callback the key callback lambda
     */
    public void setKeyCallback(KeyCallback callback) {
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> 
            callback.invoke(window, key, scancode, action, mods));
    }

    /**
     * Sets a cursor position callback for the window using a lambda expression.
     *
     * @param callback the cursor position callback lambda
     */
    public void setCursorPosCallback(CursorPosCallback callback) {
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> 
            callback.invoke(window, xpos, ypos));
    }

    /**
     * Sets a mouse button callback for the window using a lambda expression.
     *
     * @param callback the mouse button callback lambda
     */
    public void setMouseButtonCallback(MouseButtonCallback callback) {
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> 
            callback.invoke(window, button, action, mods));
    }

    /**
     * Sets a scroll callback for the window using a lambda expression.
     *
     * @param callback the scroll callback lambda
     */
    public void setScrollCallback(ScrollCallback callback) {
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> 
            callback.invoke(window, xoffset, yoffset));
    }

    /**
     * Functional interface for key callbacks.
     */
    @FunctionalInterface
    public interface KeyCallback {
        void invoke(long window, int key, int scancode, int action, int mods);
    }

    /**
     * Functional interface for cursor position callbacks.
     */
    @FunctionalInterface
    public interface CursorPosCallback {
        void invoke(long window, double xpos, double ypos);
    }

    /**
     * Functional interface for mouse button callbacks.
     */
    @FunctionalInterface
    public interface MouseButtonCallback {
        void invoke(long window, int button, int action, int mods);
    }

    /**
     * Functional interface for scroll callbacks.
     */
    @FunctionalInterface
    public interface ScrollCallback {
        void invoke(long window, double xoffset, double yoffset);
    }

    /**
     * Checks if the window should close.
     *
     * @return true if the window should close, false otherwise
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    /**
     * Sets whether the window should close.
     *
     * @param shouldClose true if the window should close, false otherwise
     */
    public void setShouldClose(boolean shouldClose) {
        glfwSetWindowShouldClose(windowHandle, shouldClose);
    }

    /**
     * Swaps the front and back buffers.
     */
    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }

    /**
     * Polls for window events.
     */
    public void pollEvents() {
        glfwPollEvents();
    }

    /**
     * Destroys the window and frees the callbacks.
     */
    public void destroy() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    /**
     * Gets the window handle.
     *
     * @return the window handle
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * Gets the width of the window.
     *
     * @return the width of the window
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the window.
     *
     * @return the height of the window
     */
    public int getHeight() {
        return height;
    }
}
