package net.fredrikmeyer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.glClearColor;

/**
 * Responsible for rendering the scene.
 */
public class Renderer {
    private final Window window;
    private final IScene scene;

    /**
     * Creates a new Renderer for the specified window and scene.
     *
     * @param window the window to render to
     * @param scene the scene to render
     */
    public Renderer(Window window, IScene scene) {
        this.window = window;
        this.scene = scene;
        init();
    }

    /**
     * Initializes the renderer.
     */
    private void init() {
        // Enable depth testing
        glEnable(GL_DEPTH_TEST);
    }

    /**
     * Renders a single frame.
     *
     * @param deltaTime the time elapsed since the last frame
     */
    public void render(float deltaTime) {
        // Clear the screen
        glClearColor(0.07f, 0.13f, 0.17f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Update and render the scene
        scene.update(deltaTime);
        scene.render();

        // Swap buffers and poll events
        window.swapBuffers();
        window.pollEvents();
    }

    /**
     * Runs the rendering loop until the window should close.
     */
    public void renderLoop() {
        // Timing variables
        long lastFrameTime = System.nanoTime();
        float deltaTime;

        // Run the rendering loop until the window should close
        while (!window.shouldClose()) {
            // Calculate delta time
            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = currentTime;

            // Render a frame
            render(deltaTime);
        }
    }
}
