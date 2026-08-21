package net.fredrikmeyer.opengl;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
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
     * Creates a new InputHandler for the specified window and registers its input callbacks.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     * @return a fully initialized InputHandler
     */
    public static InputHandler create(Window window, ScreenshotManager screenshotManager, Camera camera) {
        InputHandler handler = new InputHandler(window, screenshotManager, camera);
        handler.installCallbacks();
        return handler;
    }

    /**
     * Subclasses must be created through their own factory method, which is responsible for
     * calling {@link #installCallbacks()} once the instance is fully constructed.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     */
    protected InputHandler(Window window, ScreenshotManager screenshotManager, Camera camera) {
        this.window = window;
        this.screenshotManager = screenshotManager;
        this.camera = camera;
    }

    /**
     * Sets up input callbacks for the window.
     *
     * <p>This must not be called from a constructor: the callbacks capture {@code this} and may
     * invoke the overridable {@link #onZKeyPressed()}, which would observe a subclass whose fields
     * are not yet assigned.
     */
    protected final void installCallbacks() {
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
                // Stop recording GIF and save asynchronously
                screenshotManager.stopRecording().thenAccept(filename -> {
                    if (filename != null) {
                        System.out.println("GIF saved successfully to: " + filename);
                    } else {
                        System.out.println("Failed to save GIF");
                    }
                });
            } else if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                // Reset camera position and zoom when R is pressed
                camera.reset();
            }

            float speed = 0.1f;
            // WASD keys for movement
            if (key == GLFW_KEY_W && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveForward(speed);
            }
            if (key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveBackward(speed);
            }
            if (key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveLeft(speed);
            }
            if (key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveRight(speed);
            }

            // Arrow keys for movement
            if (key == GLFW_KEY_UP && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveForward(speed);
            }
            if (key == GLFW_KEY_DOWN && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveBackward(speed);
            }
            if (key == GLFW_KEY_LEFT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveLeft(speed);
            }
            if (key == GLFW_KEY_RIGHT && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                camera.moveRight(speed);
            }

            // Page Up/Down keys for camera angle adjustment (pitch)
            float rotationSpeed = 0.05f;
            if (key == GLFW_KEY_Q && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                // Look up
                camera.rotateVertical(rotationSpeed);
            }
            if (key == GLFW_KEY_E && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                // Look down
                camera.rotateVertical(-rotationSpeed);
            }

            // Z key for toggling auto-rotation in RayMarchingScene
            if (key == GLFW_KEY_Z && action == GLFW_RELEASE) {
                // This will be handled by the RayMarchingApp
                onZKeyPressed();
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

    /**
     * Called when the Z key is pressed. Override this method to add custom behavior.
     * This is used by the RayMarchingApp to toggle auto-rotation.
     */
    protected void onZKeyPressed() {
        // Default implementation does nothing
    }
}
