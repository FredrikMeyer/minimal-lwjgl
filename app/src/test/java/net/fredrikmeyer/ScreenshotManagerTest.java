package net.fredrikmeyer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

class ScreenshotManagerTest {
    
    @TempDir
    Path tempDir;
    
    private Path testDir;
    
    @BeforeEach
    void setUp() {
        testDir = tempDir.resolve("test-screenshots");
    }
    
    @Test
    void testConstructorCreatesDirectory() {
        // Test that the constructor creates the screenshots directory if it doesn't exist
        String dirPath = testDir.toString();
        
        // Verify directory doesn't exist yet
        assertFalse(Files.exists(testDir));
        
        // Create ScreenshotManager with custom directory
        ScreenshotManager manager = new ScreenshotManager(dirPath);
        
        // Verify directory was created
        assertTrue(Files.exists(testDir));
        assertTrue(Files.isDirectory(testDir));
    }
    
    @Test
    void testConstructorWithExistingDirectory() {
        // Test that the constructor works with an existing directory
        String dirPath = testDir.toString();
        
        // Create the directory first
        File dir = new File(dirPath);
        dir.mkdir();
        
        // Verify directory exists
        assertTrue(Files.exists(testDir));
        
        // Create ScreenshotManager with existing directory
        ScreenshotManager manager = new ScreenshotManager(dirPath);
        
        // Verify directory still exists
        assertTrue(Files.exists(testDir));
        assertTrue(Files.isDirectory(testDir));
    }
    
    @Test
    void testDefaultConstructor() {
        // Test that the default constructor uses "screenshots" as the directory
        // We can't easily verify this without modifying the class, but we can at least
        // ensure it doesn't throw an exception
        ScreenshotManager manager = new ScreenshotManager();
        
        // Verify the screenshots directory exists
        File screenshotsDir = new File("screenshots");
        assertTrue(screenshotsDir.exists());
        assertTrue(screenshotsDir.isDirectory());
        
        // Clean up - delete the directory if it was created by this test
        if (screenshotsDir.exists() && screenshotsDir.list().length == 0) {
            screenshotsDir.delete();
        }
    }
    
    // Note: We can't easily test the takeScreenshot method without an OpenGL context and GLFW window
}