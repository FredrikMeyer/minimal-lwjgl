package net.fredrikmeyer.opengl;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

/**
 * Utility class for taking screenshots in OpenGL applications.
 */
public class ScreenshotManager {

    private final String screenshotsDirectory;

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
}