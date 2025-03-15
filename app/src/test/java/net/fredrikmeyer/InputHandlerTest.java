package net.fredrikmeyer;

import org.checkerframework.checker.units.qual.C;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.lwjgl.glfw.GLFW.*;

class InputHandlerTest {

    // Mock classes for Window and ScreenshotManager
    private static class MockWindow extends Window {

        private boolean shouldClose = false;
        private Window.KeyCallback keyCallback;
        private Window.MouseButtonCallback mouseButtonCallback;
        private Window.CursorPosCallback cursorPosCallback;
        private Window.ScrollCallback scrollCallback;
        private double[] cursorPos = new double[] {0, 0};

        public MockWindow() {
            super(WindowDimensions.of(300, 300), "Mock Window", true);
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
        public void setMouseButtonCallback(Window.MouseButtonCallback callback) {
            this.mouseButtonCallback = callback;
        }

        @Override
        public void setCursorPosCallback(Window.CursorPosCallback callback) {
            this.cursorPosCallback = callback;
        }

        @Override
        public void setScrollCallback(Window.ScrollCallback callback) {
            this.scrollCallback = callback;
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

        // Method to simulate mouse button press
        public void simulateMouseButtonPress(int button, int action) {
            if (mouseButtonCallback != null) {
                mouseButtonCallback.invoke(1, button, action, 0);
            }
        }

        // Method to simulate cursor position change
        public void simulateCursorPos(double xpos, double ypos) {
            cursorPos[0] = xpos;
            cursorPos[1] = ypos;
            if (cursorPosCallback != null) {
                cursorPosCallback.invoke(1, xpos, ypos);
            }
        }

        // Method to simulate scroll
        public void simulateScroll(double xoffset, double yoffset) {
            if (scrollCallback != null) {
                scrollCallback.invoke(1, xoffset, yoffset);
            }
        }

        // Override glfwGetCursorPos to return the simulated cursor position
        @Override
        public void pollEvents() {
            // Do nothing - we don't want to poll events in tests
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

    @Test
    void testMouseMovementRotatesCamera() {
        // Test that dragging the mouse rotates the camera view
        Vector3f initialOrientation = camera.getOrientation();
        assertEquals(0, initialOrientation.x, DELTA);
        assertEquals(0, initialOrientation.y, DELTA);
        assertEquals(-1, initialOrientation.z, DELTA);

        // Verify position doesn't change
        Vector3f initialPosition = camera.getPosition();
        assertEquals(0, initialPosition.x, DELTA);
        assertEquals(0, initialPosition.y, DELTA);
        assertEquals(0, initialPosition.z, DELTA);

        // Simulate mouse button press and movement
        window.simulateMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS);
        window.simulateCursorPos(100, 100); // Initial position
        window.simulateCursorPos(200, 150); // Move right and down

        // Verify camera orientation changed but position didn't
        Vector3f newOrientation = camera.getOrientation();
        assertNotEquals(initialOrientation.x, newOrientation.x, DELTA, "Camera orientation should change");
        assertNotEquals(initialOrientation.y, newOrientation.y, DELTA, "Camera orientation should change");

        Vector3f newPosition = camera.getPosition();
        assertEquals(initialPosition.x, newPosition.x, DELTA, "Camera position should not change");
        assertEquals(initialPosition.y, newPosition.y, DELTA, "Camera position should not change");
        assertEquals(initialPosition.z, newPosition.z, DELTA, "Camera position should not change");

        // Simulate mouse button release
        window.simulateMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT, GLFW_RELEASE);
    }

    @Test
    void testMouseWheelZoomsCamera() {
        // Test that scrolling the mouse wheel zooms the camera
        float initialZoom = camera.getZoom();
        assertEquals(1.0f, initialZoom, DELTA);

        // Simulate scrolling up (zoom in)
        window.simulateScroll(0, 1);

        // Verify camera zoomed in (increased zoom)
        float newZoom = camera.getZoom();
        assertTrue(newZoom > initialZoom, "Camera should zoom in (increased zoom)");

        // Simulate scrolling down (zoom out)
        window.simulateScroll(0, -2);

        // Verify camera zoomed out (decreased zoom)
        float finalZoom = camera.getZoom();
        assertTrue(finalZoom < newZoom, "Camera should zoom out (decreased zoom)");
    }

    @Test
    void testRKeyResetsCamera() {
        // Test that pressing R key resets the camera position and zoom

        // Move and zoom the camera
        window.simulateKeyPress(GLFW_KEY_W, GLFW_REPEAT);
        window.simulateKeyPress(GLFW_KEY_D, GLFW_REPEAT);
        window.simulateScroll(0, 1);

        // Verify camera moved and zoomed
        Vector3f movedPosition = camera.getPosition();
        float zoomedZoom = camera.getZoom();
        assertTrue(movedPosition.x != 0 || movedPosition.z != 0, "Camera should have moved");
        assertTrue(zoomedZoom != 1.0f, "Camera should have zoomed");

        // Simulate pressing R key
        window.simulateKeyPress(GLFW_KEY_R, GLFW_RELEASE);

        // Verify camera reset to initial position and zoom
        Vector3f resetPosition = camera.getPosition();
        float resetZoom = camera.getZoom();
        assertEquals(0, resetPosition.x, DELTA);
        assertEquals(0, resetPosition.y, DELTA);
        assertEquals(0, resetPosition.z, DELTA);
        assertEquals(1.0f, resetZoom, DELTA);
    }
}
