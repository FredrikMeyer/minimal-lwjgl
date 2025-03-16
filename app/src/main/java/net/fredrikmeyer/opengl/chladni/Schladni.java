package net.fredrikmeyer.opengl.chladni;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform2f;

import net.fredrikmeyer.opengl.ElementBufferObject;
import net.fredrikmeyer.opengl.IScene;
import net.fredrikmeyer.opengl.Shader;
import net.fredrikmeyer.opengl.Utils;
import net.fredrikmeyer.opengl.VertexArrayObject;
import net.fredrikmeyer.opengl.VertexBufferObject;
import org.lwjgl.glfw.GLFW;

/**
 * A scene that renders an algebraic curve.
 */
public class Schladni implements IScene {

    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;
    private final Shader shader;
    private int[] quadIndices;
    private final int uParam;
    private final int uRangeId;
    private final int uTimeId;
    private float time = 0;

    /**
     * Constructs a new AlgCurveScene instance, initializing the shader and geometry for the scene.
     */
    public Schladni() {
        // Load shader
        shader = new Shader(
            Utils.loadResource("schladni/vertex.glsl"),
            Utils.loadResource("schladni/fragment.glsl"));

        // Create geometry
        createGeometry();

        // Get uniform locations
        uParam = glGetUniformLocation(shader.shaderProgram(), "uParam");
        uRangeId = glGetUniformLocation(shader.shaderProgram(), "uRange");
        uTimeId = glGetUniformLocation(shader.shaderProgram(), "uTime");
    }

    /**
     * Creates the geometry for the scene.
     */
    private void createGeometry() {
        float[] quadVertices = {
            -1.0f, -1.0f, // Bottom-left
            1.0f, -1.0f, // Bottom-right
            -1.0f, 1.0f, // Top-left
            1.0f, 1.0f, // Top-right
        };

        quadIndices = new int[]{
            0, 1, 2, // First triangle
            1, 3, 2  // Second triangle
        };

        vao = new VertexArrayObject();
        vao.bind();
        vbo = new VertexBufferObject(quadVertices);
        ebo = new ElementBufferObject(quadIndices);

        vao.linkAttributes(vbo, 0, 2, GL_FLOAT, 2 * 4, 0);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
    }

    @Override
    public void render() {
        shader.activate();

        // Set uniforms
        glUniform1f(uParam,
            (float) (Math.sin(GLFW.glfwGetTime() / 2) * Math.abs(Math.sin(GLFW.glfwGetTime() / 2))));

        // Define world range for x and y (e.g., -2 to 2)
        glUniform2f(uRangeId, -2.0f, 2.0f);
        // Define the threshold for floating-point precision
        glUniform1f(uTimeId, time);

        vao.bind();

        glDrawElements(GL_TRIANGLES, quadIndices.length, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void cleanup() {
        vao.delete();
        vbo.delete();
        ebo.delete();
        shader.delete();
    }
}