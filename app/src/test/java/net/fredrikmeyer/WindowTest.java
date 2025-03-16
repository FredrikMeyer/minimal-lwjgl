package net.fredrikmeyer;

import net.fredrikmeyer.opengl.Window;
import net.fredrikmeyer.opengl.WindowDimensions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class WindowTest {
    
    // Test subclass of Window that overrides GLFW-dependent methods
    private static class TestWindow extends Window {
        private boolean initialized = false;
        private boolean destroyed = false;
        private boolean shouldClose = false;
        private Window.KeyCallback keyCallback;
        
        public TestWindow(int width, int height, String title, boolean resizable) {
            super(WindowDimensions.of(width, height), title, resizable);
        }
        
        @Override
        public void init() {
            initialized = true;
        }
        
        @Override
        public void destroy() {
            destroyed = true;
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
        public void setKeyCallback(Window.KeyCallback callback) {
            this.keyCallback = callback;
        }
        
        @Override
        public void swapBuffers() {
            // Do nothing
        }
        
        @Override
        public void pollEvents() {
            // Do nothing
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean isDestroyed() {
            return destroyed;
        }
        
        public Window.KeyCallback getKeyCallback() {
            return keyCallback;
        }
    }
    
    private TestWindow window;
    
    @BeforeEach
    void setUp() {
        window = new TestWindow(800, 600, "Test Window", true);
    }
    
    @Test
    void testConstructor() {
        // Test that the constructor sets the window properties correctly
        assertEquals(800, window.getWidth());
        assertEquals(600, window.getHeight());
    }
    
    @Test
    void testInit() {
        // Test that init() initializes the window
        assertFalse(window.isInitialized());
        window.init();
        assertTrue(window.isInitialized());
    }
    
    @Test
    void testDestroy() {
        // Test that destroy() destroys the window
        assertFalse(window.isDestroyed());
        window.destroy();
        assertTrue(window.isDestroyed());
    }
    
    @Test
    void testShouldClose() {
        // Test that shouldClose() returns the correct value
        assertFalse(window.shouldClose());
        window.setShouldClose(true);
        assertTrue(window.shouldClose());
    }
    
    @Test
    void testSetKeyCallback() {
        // Test that setKeyCallback() sets the key callback
        assertNull(window.getKeyCallback());
        
        Window.KeyCallback callback = (windowHandle, key, scancode, action, mods) -> {
            // Do nothing
        };
        
        window.setKeyCallback(callback);
        assertSame(callback, window.getKeyCallback());
    }
}