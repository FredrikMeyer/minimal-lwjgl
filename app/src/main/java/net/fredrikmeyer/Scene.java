package net.fredrikmeyer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

/**
 * Represents a 3D scene with models, textures, and transformations.
 */
public class Scene {
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;
    private Shader shader;
    private Texture texture;
    private int[] indices;

    private float rotation = 0.0f;
    private float scale = 0.5f;
    private float aspectRatio;

    /**
     * Creates a new Scene with the specified resources.
     *
     * @param resourceLoader the resource loader to use for loading resources
     * @param aspectRatio the aspect ratio (width/height) to use for rendering
     */
    public Scene(ResourceLoader resourceLoader, float aspectRatio) {
        this.aspectRatio = aspectRatio;
        // Load shader
        shader = resourceLoader.loadShader("triangle/vertex.glsl", "triangle/fragment.glsl");

        // Create geometry
        createGeometry();

        // Load texture
        texture = resourceLoader.loadTexture("icon.png");
    }

    /**
     * Creates the geometry for the scene.
     */
    private void createGeometry() {
        float[] vertices = new float[] { 
            //     COORDINATES     /        COLORS      /   TexCoord  //
            -0.5f, 0.0f,  0.5f,     0.83f, 0.70f, 0.44f,    0.0f, 0.0f,
            -0.5f, 0.0f, -0.5f,     0.83f, 0.70f, 0.44f,    5.0f, 0.0f,
             0.5f, 0.0f, -0.5f,     0.83f, 0.70f, 0.44f,    0.0f, 0.0f,
             0.5f, 0.0f,  0.5f,     0.83f, 0.70f, 0.44f,    5.0f, 0.0f,
             0.0f, 0.8f,  0.0f,     0.92f, 0.86f, 0.76f,    2.5f, 5.0f
        };

        indices = new int[] {
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
        rotation += 0.01f;
        // Other updates can be added here
    }

    /**
     * Renders the scene.
     */
    public void render() {
        shader.activate();

        // Create model matrix
        FloatBuffer fbModel = BufferUtils.createFloatBuffer(16);
        Matrix4f modelMatrix = new Matrix4f().rotate(rotation, 0, 1, 0);
        modelMatrix.get(fbModel);

        // Create projection matrix
        FloatBuffer fbProjection = BufferUtils.createFloatBuffer(16);
        Matrix4f projectionMatrix = new Matrix4f().perspective(45, aspectRatio, 0.1f, 100.0f);
        projectionMatrix.get(fbProjection);

        // Create view matrix
        FloatBuffer fbView = BufferUtils.createFloatBuffer(16);
        Matrix4f viewMatrix = new Matrix4f().translate(new Vector3f(0.0f, -0.5f, -2.0f));
        viewMatrix.get(fbView);

        // Set uniforms
        var modelLoc = glGetUniformLocation(shader.shaderProgram(), "model");
        glUniformMatrix4fv(modelLoc, false, fbModel);
        var viewLoc = glGetUniformLocation(shader.shaderProgram(), "view");
        glUniformMatrix4fv(viewLoc, false, fbView);
        var projLoc = glGetUniformLocation(shader.shaderProgram(), "proj");
        glUniformMatrix4fv(projLoc, false, fbProjection);

        var uniId = glGetUniformLocation(shader.shaderProgram(), "scale");
        glUniform1f(uniId, (float) (Math.sin(scale) + 1));

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
