package net.fredrikmeyer.opengl.algcurve;

import net.fredrikmeyer.opengl.WindowDimensions;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.fredrikmeyer.opengl.IScene;
import net.fredrikmeyer.opengl.Renderer;
import net.fredrikmeyer.opengl.Window;

/**
 * Main application class for the algebraic curve visualization.
 */
public class App {

    private Window window;
    private IScene scene;
    private Renderer renderer;

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
        window = new Window(WindowDimensions.of(600, 600), "Algebraic Curve Visualization", true);
        window.init();

        // Create the scene
        scene = new net.fredrikmeyer.opengl.algcurve.AlgCurveScene();

        // Create renderer
        renderer = new Renderer(window, scene);

        // Set up key callback for ESC key
        window.setKeyCallback((window, key, scancode, action, mods) -> {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && action == org.lwjgl.glfw.GLFW.GLFW_RELEASE) {
                org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose(window, true);
            }
        });
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