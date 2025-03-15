package net.fredrikmeyer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 * Represents a 3D scene with models, textures, and transformations.
 */
public class Scene {

    private final Camera camera;
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;
    private final Shader shader;
    private final Texture texture;
    private int[] indices;

    private float rotation = 0.0f;

    /**
     * Creates a new Scene with the specified resources.
     *
     * @param resourceLoader the resource loader to use for loading resources
     * @param camera
     */
    public Scene(ResourceLoader resourceLoader, Camera camera) {
        // Load shader
        shader = resourceLoader.loadShader("triangle/vertex.glsl", "triangle/fragment.glsl");

        // Create geometry
        createGeometry();

        // Load texture
        texture = resourceLoader.loadTexture("icon.png");
        this.camera = camera;
    }

    /**
     * Creates the geometry for the scene.
     */
    private void createGeometry() {
        float[] vertices = new float[]{
            //     COORDINATES     /        COLORS      /   TexCoord  //
            -0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f,
            -0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f,
            0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f,
            0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f,
            0.0f, 0.8f, 0.0f, 0.92f, 0.86f, 0.76f, 2.5f, 5.0f
        };

        indices = new int[]{
            0, 1, 2,
            0, 2, 3,
            0, 1, 4,
            1, 2, 4,
            2, 3, 4,
            3, 0, 4
        };

        vao = new VertexArrayObject();
        vao.bind();
        vbo = new VertexBufferObject(vertices);
        ebo = new ElementBufferObject(indices);

        vao.linkAttributes(vbo, 0, 3, GL_FLOAT, 8 * 4, 0);
        vao.linkAttributes(vbo, 1, 3, GL_FLOAT, 8 * 4, 3 * 4);
        vao.linkAttributes(vbo, 2, 2, GL_FLOAT, 8 * 4, 6 * 4);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
    }

    /**
     * Updates the scene for the current frame.
     *
     * @param deltaTime the time elapsed since the last frame
     */
    public void update(float deltaTime) {
        rotation += deltaTime * 40;
        // Other updates can be added here
    }

    /**
     * Renders the scene.
     */
    public void render() {
        shader.activate();

        // Set uniforms
        var camMatrix = camera.matrix(45f, 0.1f, 100f)
            .rotateY((float) Math.toRadians(rotation));
        FloatBuffer camMatrixFB = BufferUtils.createFloatBuffer(16);
        camMatrix.get(camMatrixFB);

        var camMatrixLoc = glGetUniformLocation(shader.shaderProgram(), "camMatrix");
        glUniformMatrix4fv(camMatrixLoc, false, camMatrixFB);

        // Bind texture and VAO
        texture.bind();
        vao.bind();

        // Draw elements
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    /**
     * Cleans up resources used by the scene.
     */
    public void cleanup() {
        vao.delete();
        vbo.delete();
        ebo.delete();
        shader.delete();
        texture.cleanup();
    }
}
