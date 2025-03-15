package net.fredrikmeyer;

import org.checkerframework.checker.units.qual.C;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

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
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        window = new MockWindow();
        screenshotManager = new MockScreenshotManager();
        inputHandler = new InputHandler(window, screenshotManager, new Camera(1f, new Vector3f()));
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
}