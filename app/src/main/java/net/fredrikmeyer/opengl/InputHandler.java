package net.fredrikmeyer.opengl;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

/**
 * Handles input events for the application.
 */
public class InputHandler {

    private final Window window;
    private final ScreenshotManager screenshotManager;
    private final Camera camera;

    // Mouse state
    private boolean mouseLeftButtonPressed = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private double mouseSensitivity = 0.005;
    private double scrollSensitivity = 0.1;

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
            } else if (key == GLFW_KEY_F3 && action == GLFW_RELEASE) {
                // Start recording GIF
                screenshotManager.startRecording();
            } else if (key == GLFW_KEY_F4 && action == GLFW_RELEASE) {
                // Stop recording GIF and save
                screenshotManager.stopRecording();
            } else if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                // Reset camera position and zoom when R is pressed
                camera.reset();
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

        // Set up mouse button callback
        window.setMouseButtonCallback((windowHandle, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    mouseLeftButtonPressed = true;
                    // Initial mouse position will be set by the first cursor position callback
                } else if (action == GLFW_RELEASE) {
                    mouseLeftButtonPressed = false;
                }
            }
        });

        // Set up cursor position callback
        window.setCursorPosCallback((windowHandle, xpos, ypos) -> {
            if (mouseLeftButtonPressed) {
                // If this is the first cursor position event after the button was pressed,
                // just store the position without moving the camera
                if (lastMouseX == 0 && lastMouseY == 0) {
                    lastMouseX = xpos;
                    lastMouseY = ypos;
                    return;
                }

                // Calculate mouse movement
                double deltaX = xpos - lastMouseX;
                double deltaY = ypos - lastMouseY;

                // Rotate camera view based on mouse movement
                camera.rotateHorizontal((float) (-deltaX * mouseSensitivity));
                camera.rotateVertical((float) (-deltaY * mouseSensitivity));

                // Update last mouse position
                lastMouseX = xpos;
                lastMouseY = ypos;
            } else {
                // Reset last mouse position when not pressed
                lastMouseX = 0;
                lastMouseY = 0;
            }
        });

        // Set up scroll callback
        window.setScrollCallback((windowHandle, xoffset, yoffset) -> {
            // Zoom camera based on scroll wheel
            camera.zoom((float) (yoffset * scrollSensitivity));
        });
    }
}
