package net.fredrikmeyer.opengl.algsurface;

import net.fredrikmeyer.opengl.Camera;
import net.fredrikmeyer.opengl.IScene;
import net.fredrikmeyer.opengl.InputHandler;
import net.fredrikmeyer.opengl.Renderer;
import net.fredrikmeyer.opengl.ScreenshotManager;
import net.fredrikmeyer.opengl.Window;
import net.fredrikmeyer.opengl.WindowDimensions;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * Main application class for the algebraic surface visualization.
 * The surface is defined by the equation: x³y+xz³+y³z+z³+7z²+5z=0
 */
public class App {

    private Window window;
    private IScene scene;
    private Renderer renderer;
    private InputHandler inputHandler;
    private ScreenshotManager screenshotManager;
    private Camera camera;

    /**
     * Runs the application.
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            mainLoop();
            cleanup();
        } finally {
            // Terminate GLFW and free the error callback
            org.lwjgl.glfw.GLFW.glfwTerminate();
            GLFWErrorCallback callback = org.lwjgl.glfw.GLFW.glfwSetErrorCallback(null);
            if (callback != null) {
                callback.free();
            }
        }
    }

    /**
     * Initializes the application components.
     */
    private void init() {
        // Create the window
        window = new Window(WindowDimensions.of(600, 600), "Algebraic Surface Visualization", true);
        window.init();

        // Create the screenshot manager
        screenshotManager = new ScreenshotManager();

        // Create the camera with the window's aspect ratio
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        camera = new Camera(aspectRatio, new Vector3f(0f, 0f, 5f));

        // Create the scene
        AlgSurfaceScene algSurfaceScene = new AlgSurfaceScene(camera);
        scene = algSurfaceScene;

        // Create the custom input handler that supports toggling auto-rotation with the Z key
        inputHandler = new AlgSurfaceInputHandler(window, screenshotManager, camera, algSurfaceScene);

        // Create renderer
        renderer = new Renderer(window, scene, screenshotManager);
    }

    /**
     * Runs the main application loop.
     */
    private void mainLoop() {
        // Run the rendering loop
        renderer.renderLoop();
    }

    /**
     * Cleans up resources used by the application.
     */
    private void cleanup() {
        scene.cleanup();
        window.destroy();
        screenshotManager.cleanup();
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new App().run();
    }
}