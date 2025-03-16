package net.fredrikmeyer.opengl;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

/**
 * Utility class for taking screenshots in OpenGL applications.
 */
public class ScreenshotManager {

    private final String screenshotsDirectory;
    private boolean isRecording = false;
    private List<BufferedImage> recordedFrames = new ArrayList<>();
    private static final int FRAME_DELAY_MS = 50; // 20 frames per second
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Creates a new ScreenshotManager with the default screenshots directory.
     */
    public ScreenshotManager() {
        this("screenshots");
    }

    /**
     * Creates a new ScreenshotManager with a custom screenshots directory.
     *
     * @param screenshotsDirectory The directory where screenshots will be saved
     */
    public ScreenshotManager(String screenshotsDirectory) {
        this.screenshotsDirectory = screenshotsDirectory;

        // Create the screenshots directory if it doesn't exist
        File directory = new File(screenshotsDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    /**
     * Takes a screenshot of the current window and saves it as a PNG file. The file is saved in the
     * screenshots directory with a timestamp in the filename.
     *
     * @param window The GLFW window handle
     * @return The path to the saved screenshot file, or null if the screenshot failed
     */
    public String takeScreenshot(long window) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);

            // Get framebuffer size (actual size of the window content)
            glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            int width = widthBuffer.get(0);
            int height = heightBuffer.get(0);

            // Create buffer to store pixel data
            ByteBuffer buffer = BufferUtils.createByteBuffer(
                width * height * 4); // 4 bytes per pixel (RGBA)

            // Read pixels from framebuffer
            glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            // Create a new buffer for the flipped image
            ByteBuffer flippedBuffer = BufferUtils.createByteBuffer(width * height * 4);

            // Flip the image vertically (OpenGL reads from bottom-left, but images typically start from top-left)
            for (int y = 0; y < height; y++) {
                int srcRow = height - 1 - y;
                for (int x = 0; x < width; x++) {
                    int srcIndex = (srcRow * width + x) * 4;
                    int dstIndex = (y * width + x) * 4;

                    // Set position and copy RGBA values
                    buffer.position(srcIndex);
                    flippedBuffer.position(dstIndex);

                    flippedBuffer.put(buffer.get());  // R
                    flippedBuffer.put(buffer.get());  // G
                    flippedBuffer.put(buffer.get());  // B
                    flippedBuffer.put(buffer.get());  // A
                }
            }

            // Reset position to the beginning of buffer
            flippedBuffer.position(0);

            // Generate filename with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String filename = screenshotsDirectory + "/screenshot_" + timestamp + ".png";

            // Save the flipped image
            stbi_write_png(filename, width, height, 4, flippedBuffer, width * 4);

            System.out.println("Screenshot saved to: " + filename);
            return filename;
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Starts recording frames for a GIF animation.
     */
    public void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordedFrames.clear();
            System.out.println("GIF recording started");
        }
    }

    /**
     * Stops recording frames and saves the GIF animation asynchronously.
     * 
     * @return A CompletableFuture that will be completed with the path to the saved GIF file, or null if saving failed
     */
    public CompletableFuture<String> stopRecording() {
        if (isRecording) {
            isRecording = false;
            System.out.println("GIF recording stopped, saving " + recordedFrames.size() + " frames asynchronously");
            return saveGifAsync();
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Captures a frame for the GIF animation if recording is active.
     * 
     * @param window The GLFW window handle
     */
    public void captureFrame(long window) {
        if (isRecording) {
            try (MemoryStack stack = stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);

                // Get framebuffer size
                glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
                int width = widthBuffer.get(0);
                int height = heightBuffer.get(0);

                // Create buffer to store pixel data
                ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

                // Read pixels from framebuffer
                glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

                // Create a BufferedImage
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                // Flip the image vertically and copy pixel data
                for (int y = 0; y < height; y++) {
                    int srcRow = height - 1 - y;
                    for (int x = 0; x < width; x++) {
                        int srcIndex = (srcRow * width + x) * 4;

                        // Set position and get RGBA values
                        buffer.position(srcIndex);
                        int r = buffer.get() & 0xFF;
                        int g = buffer.get() & 0xFF;
                        int b = buffer.get() & 0xFF;
                        int a = buffer.get() & 0xFF;

                        // Pack RGBA into a single int
                        int rgba = (a << 24) | (r << 16) | (g << 8) | b;

                        // Set pixel in the BufferedImage
                        image.setRGB(x, y, rgba);
                    }
                }

                // Add the frame to the list
                recordedFrames.add(image);
            } catch (Exception e) {
                System.err.println("Failed to capture frame: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the recorded frames as a GIF animation.
     * 
     * @return The path to the saved GIF file, or null if saving failed
     */
    private String saveGif() {
        if (recordedFrames.isEmpty()) {
            System.out.println("No frames to save");
            return null;
        }

        try {
            // Generate filename with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String filename = screenshotsDirectory + "/animation_" + timestamp + ".gif";

            // Create output stream
            File outputFile = new File(filename);
            ImageOutputStream outputStream = new FileImageOutputStream(outputFile);

            // Get dimensions from the first frame
            BufferedImage firstFrame = recordedFrames.get(0);

            // Create GIF writer
            GifSequenceWriter gifWriter = new GifSequenceWriter(
                    outputStream,
                    firstFrame.getType(),
                    FRAME_DELAY_MS,
                    true);  // Loop continuously

            // Write all frames
            for (BufferedImage frame : recordedFrames) {
                gifWriter.writeToSequence(frame);
            }

            // Close the writer
            gifWriter.close();
            outputStream.close();

            System.out.println("GIF animation saved to: " + filename);
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to save GIF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves the recorded frames as a GIF animation asynchronously.
     * 
     * @return A CompletableFuture that will be completed with the path to the saved GIF file, or null if saving failed
     */
    private CompletableFuture<String> saveGifAsync() {
        if (recordedFrames.isEmpty()) {
            System.out.println("No frames to save");
            return CompletableFuture.completedFuture(null);
        }

        // Create a copy of the frames to avoid concurrent modification
        final List<BufferedImage> framesCopy = new ArrayList<>(recordedFrames);

        // Submit the task to the executor service
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Generate filename with timestamp
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String timestamp = dateFormat.format(new Date());
                String filename = screenshotsDirectory + "/animation_" + timestamp + ".gif";

                // Create output stream
                File outputFile = new File(filename);
                ImageOutputStream outputStream = new FileImageOutputStream(outputFile);

                // Get dimensions from the first frame
                BufferedImage firstFrame = framesCopy.get(0);

                // Create GIF writer
                GifSequenceWriter gifWriter = new GifSequenceWriter(
                        outputStream,
                        firstFrame.getType(),
                        FRAME_DELAY_MS,
                        true);  // Loop continuously

                // Write all frames
                for (BufferedImage frame : framesCopy) {
                    gifWriter.writeToSequence(frame);
                }

                // Close the writer
                gifWriter.close();
                outputStream.close();

                System.out.println("GIF animation saved to: " + filename);
                return filename;
            } catch (IOException e) {
                System.err.println("Failed to save GIF: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }, executorService);
    }

    /**
     * Checks if recording is currently active.
     * 
     * @return true if recording is active, false otherwise
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Cleans up resources used by the ScreenshotManager.
     * This should be called when the application is shutting down.
     */
    public void cleanup() {
        executorService.shutdown();
    }
}
