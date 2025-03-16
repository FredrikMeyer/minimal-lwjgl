package net.fredrikmeyer.opengl;

/**
 * Interface for a 3D scene with models, textures, and transformations.
 * This interface defines the common functionality that all scenes should have.
 */
public interface IScene {
    /**
     * Updates the scene for the current frame.
     *
     * @param deltaTime the time elapsed since the last frame
     */
    void update(float deltaTime);

    /**
     * Renders the scene.
     */
    void render();

    /**
     * Cleans up resources used by the scene.
     */
    void cleanup();
}