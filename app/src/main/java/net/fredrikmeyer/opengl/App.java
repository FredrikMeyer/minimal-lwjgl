package net.fredrikmeyer.opengl;

import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

/**
 * Main application class that coordinates the components of the OpenGL application.
 */
public class App {

    private Window window;
    private ResourceLoader resourceLoader;
    private Scene scene;
    private Renderer renderer;
    private InputHandler inputHandler;
    private ScreenshotManager screenshotManager;

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
        window = new Window(WindowDimensions.of(600, 600), "Hello World!", true);
        window.init();

        // Create resource loader
        resourceLoader = new ResourceLoader();

        // Create the screenshot manager
        screenshotManager = new ScreenshotManager();

        // Create the scene with the window's aspect ratio
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        Camera camera = new Camera(aspectRatio, new Vector3f(0f, 0f, 2f));
        scene = new Scene(resourceLoader, camera);

        // Create the input handler
        inputHandler = new InputHandler(window, screenshotManager, camera);

        // Create renderer
        renderer = new Renderer(window, scene);
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
