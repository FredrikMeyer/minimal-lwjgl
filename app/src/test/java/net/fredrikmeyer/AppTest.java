package net.fredrikmeyer;

import net.fredrikmeyer.opengl.App;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the App class.
 * 
 * Note: The App class is tightly coupled with GLFW and OpenGL, making it challenging to test
 * without initializing these libraries. These tests focus on verifying that the App class
 * can be instantiated without errors.
 */
class AppTest {

    @Test
    void testAppCanBeInstantiated() {
        // Test that an App instance can be created without errors
        App app = new App();
        assertNotNull(app);
    }

    // Note: Testing the run() method would require initializing GLFW and OpenGL,
    // which is not practical in a unit test. Integration tests would be more
    // appropriate for testing the full application lifecycle.
}
