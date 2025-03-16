package net.fredrikmeyer;

import net.fredrikmeyer.opengl.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class UtilsTest {

    @TempDir
    Path tempDir;

    private Path resourcesDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary resources directory
        resourcesDir = tempDir.resolve("resources");
        Files.createDirectories(resourcesDir);
    }

    @Test
    void testLoadResource() throws IOException {
        // Create a test text file
        String testContent = "This is a test resource file.\nIt has multiple lines.";
        Path testFile = resourcesDir.resolve("test.txt");
        Files.writeString(testFile, testContent);

        // We can't directly test Utils.loadResource because it uses ClassLoader.getResourceAsStream
        // which looks for resources in the classpath. Instead, we'll verify that the method
        // correctly throws an exception when a resource is not found.
        assertThrows(RuntimeException.class, () -> {
            Utils.loadResource("nonexistent-resource.txt");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }

    @Test
    void testLoadResourceByteBuffer() {
        // Similar to testLoadResource, we can't directly test this method with our temp files
        // But we can verify it throws the expected exception for non-existent resources
        assertThrows(RuntimeException.class, () -> {
            Utils.loadResourceByteBuffer("nonexistent-resource.bin");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }

    // Note: To properly test these methods, we would need to add test resources to the test classpath
    // or use a framework that allows us to mock the ClassLoader. For now, we're just testing
    // the error handling behavior.
}
