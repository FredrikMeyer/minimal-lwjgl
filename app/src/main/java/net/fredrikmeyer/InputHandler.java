package net.fredrikmeyer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

/**
 * Handles input events for the application.
 */
public class InputHandler {

    private final Window window;
    private final ScreenshotManager screenshotManager;
    private final Camera camera;

    /**
     * Creates a new InputHandler for the specified window.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     */
    public InputHandler(Window window, ScreenshotManager screenshotManager, Camera camera) {
        this.window = window;
        this.screenshotManager = screenshotManager;
        this.camera = camera;
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
            float speed = 0.1f;
            if (key == GLFW_KEY_W && action == GLFW_REPEAT) {
                camera.moveForward(speed);
            }
            if (key == GLFW_KEY_S && action == GLFW_REPEAT) {
                camera.moveBackward(speed);
            }
            if (key == GLFW_KEY_A && action == GLFW_REPEAT) {
                camera.moveLeft(speed);
            }
            if (key == GLFW_KEY_D && action == GLFW_REPEAT) {
                camera.moveRight(speed);
            }

        });
    }
}