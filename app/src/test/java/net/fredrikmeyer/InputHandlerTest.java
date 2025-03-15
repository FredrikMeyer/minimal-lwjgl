package net.fredrikmeyer;

import org.checkerframework.checker.units.qual.C;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

class InputHandlerTest {

    // Mock classes for Window and ScreenshotManager
    private static class MockWindow extends Window {

        private boolean shouldClose = false;
        private Window.KeyCallback keyCallback;

        public MockWindow() {
            super(300, 300, "Mock Window", true);
        }

        @Override
        public void init() {
            // Do nothing - we don't want to initialize GLFW in tests
        }

        @Override
        public void setKeyCallback(Window.KeyCallback callback) {
            this.keyCallback = callback;
        }

        @Override
        public boolean shouldClose() {
            return shouldClose;
        }

        @Override
        public void setShouldClose(boolean shouldClose) {
            this.shouldClose = shouldClose;
        }

        @Override
        public long getWindowHandle() {
            return 1; // Dummy window handle
        }

        // Method to simulate key press
        public void simulateKeyPress(int key, int action) {
            if (keyCallback != null) {
                keyCallback.invoke(1, key, 0, action, 0);
            }
        }
    }

    private static class MockScreenshotManager extends ScreenshotManager {

        private boolean screenshotTaken = false;
        private long lastWindowHandle = 0;

        @Override
        public String takeScreenshot(long window) {
            screenshotTaken = true;
            lastWindowHandle = window;
            return "mock-screenshot.png";
        }

        public boolean wasScreenshotTaken() {
            return screenshotTaken;
        }

        public long getLastWindowHandle() {
            return lastWindowHandle;
        }
    }

    private MockWindow window;
    private MockScreenshotManager screenshotManager;
    private Camera camera;
    private InputHandler inputHandler;
    private static final float DELTA = 0.0001f;

    @BeforeEach
    void setUp() {
        window = new MockWindow();
        screenshotManager = new MockScreenshotManager();
        // Create a camera at position (0, 0, 0) with aspect ratio 1.0
        camera = new Camera(1.0f, new Vector3f(0, 0, 0));
        inputHandler = new InputHandler(window, screenshotManager, camera);
    }

    @Test
    void testEscapeKeyClosesWindow() {
        // Test that pressing Escape key sets window to close
        assertFalse(window.shouldClose());

        // Simulate pressing Escape key
        window.simulateKeyPress(GLFW_KEY_ESCAPE, GLFW_RELEASE);

        // Verify window is set to close
        assertTrue(window.shouldClose());
    }

    @Test
    void testF2KeyTakesScreenshot() {
        // Test that pressing F2 key takes a screenshot
        assertFalse(screenshotManager.wasScreenshotTaken());

        // Simulate pressing F2 key
        window.simulateKeyPress(GLFW_KEY_F2, GLFW_RELEASE);

        // Verify screenshot was taken
        assertTrue(screenshotManager.wasScreenshotTaken());
        assertEquals(1, screenshotManager.getLastWindowHandle());
    }

    @Test
    void testWKeyMovesForward() {
        // Test that pressing W key moves the camera forward
        Vector3f initialPosition = camera.getPosition();
        assertEquals(0, initialPosition.x, DELTA);
        assertEquals(0, initialPosition.y, DELTA);
        assertEquals(0, initialPosition.z, DELTA);

        // Simulate pressing W key
        window.simulateKeyPress(GLFW_KEY_W, GLFW_REPEAT);

        // Verify camera moved forward (decreased Z)
        Vector3f newPosition = camera.getPosition();
        assertEquals(0, newPosition.x, DELTA);
        assertEquals(0, newPosition.y, DELTA);
        assertTrue(newPosition.z < 0, "Camera should move forward (negative Z)");
    }

    @Test
    void testSKeyMovesBackward() {
        // Test that pressing S key moves the camera backward
        Vector3f initialPosition = camera.getPosition();
        assertEquals(0, initialPosition.x, DELTA);
        assertEquals(0, initialPosition.y, DELTA);
        assertEquals(0, initialPosition.z, DELTA);

        // Simulate pressing S key
        window.simulateKeyPress(GLFW_KEY_S, GLFW_REPEAT);

        // Verify camera moved backward (increased Z)
        Vector3f newPosition = camera.getPosition();
        assertEquals(0, newPosition.x, DELTA);
        assertEquals(0, newPosition.y, DELTA);
        assertTrue(newPosition.z > 0, "Camera should move backward (positive Z)");
    }

    @Test
    void testAKeyMovesLeft() {
        // Test that pressing A key moves the camera left
        Vector3f initialPosition = camera.getPosition();
        assertEquals(0, initialPosition.x, DELTA);
        assertEquals(0, initialPosition.y, DELTA);
        assertEquals(0, initialPosition.z, DELTA);

        // Simulate pressing A key
        window.simulateKeyPress(GLFW_KEY_A, GLFW_REPEAT);

        // Verify camera moved left (decreased X)
        Vector3f newPosition = camera.getPosition();
        assertTrue(newPosition.x < 0, "Camera should move left (negative X)");
        assertEquals(0, newPosition.y, DELTA);
        assertEquals(0, newPosition.z, DELTA);
    }

    @Test
    void testDKeyMovesRight() {
        // Test that pressing D key moves the camera right
        Vector3f initialPosition = camera.getPosition();
        assertEquals(0, initialPosition.x, DELTA);
        assertEquals(0, initialPosition.y, DELTA);
        assertEquals(0, initialPosition.z, DELTA);

        // Simulate pressing D key
        window.simulateKeyPress(GLFW_KEY_D, GLFW_REPEAT);

        // Verify camera moved right (increased X)
        Vector3f newPosition = camera.getPosition();
        assertTrue(newPosition.x > 0, "Camera should move right (positive X)");
        assertEquals(0, newPosition.y, DELTA);
        assertEquals(0, newPosition.z, DELTA);
    }
}
