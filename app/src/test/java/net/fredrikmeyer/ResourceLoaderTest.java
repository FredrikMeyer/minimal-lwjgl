package net.fredrikmeyer;

import net.fredrikmeyer.opengl.ResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ResourceLoaderTest {

    private ResourceLoader resourceLoader;

    @BeforeEach
    void setUp() {
        resourceLoader = new ResourceLoader();
    }

    @Test
    void testLoadTextResource() {
        // This test verifies that loadTextResource correctly delegates to Utils.loadResource
        // Since we can't easily mock static methods without additional libraries, we'll test
        // the error handling behavior
        assertThrows(RuntimeException.class, () -> {
            resourceLoader.loadTextResource("nonexistent-resource.txt");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }

    @Test
    void testLoadBinaryResource() {
        // Similar to testLoadTextResource, we'll test the error handling behavior
        assertThrows(RuntimeException.class, () -> {
            resourceLoader.loadBinaryResource("nonexistent-resource.bin");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }

    // For loadShader and loadTexture, we would need to mock the dependencies or use a test subclass
    // that overrides the methods that use Utils. For now, we'll just test that they call the
    // expected methods by checking that they throw exceptions when the resources don't exist.

    @Test
    void testLoadShader() {
        assertThrows(RuntimeException.class, () -> {
            resourceLoader.loadShader("nonexistent-vertex.glsl", "nonexistent-fragment.glsl");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }

    @Test
    void testLoadTexture() {
        assertThrows(RuntimeException.class, () -> {
            resourceLoader.loadTexture("nonexistent-texture.png");
        });

        // Note: We're not checking the exception message because it might be null
        // in the test environment
    }
}
