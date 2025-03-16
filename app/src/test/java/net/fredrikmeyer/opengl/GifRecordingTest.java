package net.fredrikmeyer.opengl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Tests for the GIF recording functionality in ScreenshotManager.
 */
class GifRecordingTest {

    @TempDir
    Path tempDir;

    private Path testDir;
    private ScreenshotManager manager;

    @BeforeEach
    void setUp() {
        testDir = tempDir.resolve("test-screenshots");
        manager = new ScreenshotManager(testDir.toString());
    }

    @Test
    void testStartRecording() throws Exception {
        // Test that startRecording sets isRecording to true and clears recordedFrames

        // Use reflection to access private fields
        Field isRecordingField = ScreenshotManager.class.getDeclaredField("isRecording");
        isRecordingField.setAccessible(true);

        Field recordedFramesField = ScreenshotManager.class.getDeclaredField("recordedFrames");
        recordedFramesField.setAccessible(true);

        // Verify initial state
        assertFalse((Boolean) isRecordingField.get(manager));

        // Start recording
        manager.startRecording();

        // Verify recording state
        assertTrue((Boolean) isRecordingField.get(manager));

        // Verify recordedFrames is empty
        List<?> recordedFrames = (List<?>) recordedFramesField.get(manager);
        assertTrue(recordedFrames.isEmpty());
    }

    @Test
    void testStopRecordingWhenNotRecording() throws ExecutionException, InterruptedException {
        // Test that stopRecording returns a CompletableFuture that completes with null when not recording
        CompletableFuture<String> future = manager.stopRecording();
        String result = future.get();  // Wait for the future to complete
        assertNull(result);
    }

    @Test
    void testIsRecording() throws Exception {
        // Test that isRecording returns the correct value

        // Use reflection to access private field
        Field isRecordingField = ScreenshotManager.class.getDeclaredField("isRecording");
        isRecordingField.setAccessible(true);

        // Verify initial state
        assertFalse(manager.isRecording());
        assertFalse((Boolean) isRecordingField.get(manager));

        // Start recording
        manager.startRecording();

        // Verify recording state
        assertTrue(manager.isRecording());
        assertTrue((Boolean) isRecordingField.get(manager));

        // Stop recording and wait for it to complete
        CompletableFuture<String> future = manager.stopRecording();
        future.join();  // Wait for the future to complete without getting the result

        // Verify recording state
        assertFalse(manager.isRecording());
        assertFalse((Boolean) isRecordingField.get(manager));
    }

    @Test
    void testStartRecordingTwice() throws Exception {
        // Test that calling startRecording twice doesn't change the state

        // Use reflection to access private field
        Field isRecordingField = ScreenshotManager.class.getDeclaredField("isRecording");
        isRecordingField.setAccessible(true);

        // Start recording
        manager.startRecording();
        assertTrue((Boolean) isRecordingField.get(manager));

        // Start recording again
        manager.startRecording();
        assertTrue((Boolean) isRecordingField.get(manager));
    }

    @Test
    void testCleanup() throws Exception {
        // Test that cleanup shuts down the ExecutorService

        // Use reflection to access private field
        Field executorServiceField = ScreenshotManager.class.getDeclaredField("executorService");
        executorServiceField.setAccessible(true);

        // Get the ExecutorService
        ExecutorService executorService = (ExecutorService) executorServiceField.get(manager);

        // Verify initial state
        assertFalse(executorService.isShutdown());

        // Call cleanup
        manager.cleanup();

        // Verify that the ExecutorService is shut down
        assertTrue(executorService.isShutdown());
    }

    // Note: We can't easily test the captureFrame and saveGif methods without an OpenGL context and GLFW window
}
