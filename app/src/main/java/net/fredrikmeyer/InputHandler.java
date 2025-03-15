package net.fredrikmeyer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * Handles input events for the application.
 */
public class InputHandler {
    private final Window window;
    private final ScreenshotManager screenshotManager;

    /**
     * Creates a new InputHandler for the specified window.
     *
     * @param window the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     */
    public InputHandler(Window window, ScreenshotManager screenshotManager) {
        this.window = window;
        this.screenshotManager = screenshotManager;
        setupCallbacks();
    }

    /**
     * Sets up input callbacks for the window.
     */
    private void setupCallbacks() {
        // Set up a key callback
        window.setKeyCallback((windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                window.setShouldClose(true);
            } else if (key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
                screenshotManager.takeScreenshot(window.getWindowHandle());
            }
        });
    }
}