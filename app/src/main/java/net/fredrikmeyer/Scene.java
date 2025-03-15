package net.fredrikmeyer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

/**
 * Represents a 3D scene with models, textures, and transformations.
 */
public class Scene implements IScene {

    private final Camera camera;
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;
    private final Shader shader;
    private final Shader lightShader;
    private final Texture texture;
    private int[] indices;

    private float rotation = 0.0f;
    private VertexArrayObject lightVao;
    private VertexBufferObject lightVbo;
    private ElementBufferObject lightEbo;

    /**
     * Constructs a new Scene instance, initializing the shader, geometry, texture, and camera for
     * the scene.
     *
     * @param resourceLoader the ResourceLoader used to load shaders and textures
     * @param camera         the Camera object used to define the view and orientation of the scene
     */
    public Scene(ResourceLoader resourceLoader, Camera camera) {
        // Load shader
        shader = resourceLoader.loadShader("triangle/vertex.glsl", "triangle/fragment.glsl");
        lightShader = resourceLoader.loadShader("triangle/lightVertex.glsl",
            "triangle/lightFragment.glsl");

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
        float[] vertices =
            { //     COORDINATES     /        COLORS          /    TexCoord   /        NORMALS       //
                -0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                // Bottom side
                -0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 5.0f, 0.0f, -1.0f, 0.0f,
                // Bottom side
                0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 5.0f, 0.0f, -1.0f, 0.0f,
                // Bottom side
                0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f, 0.0f, -1.0f, 0.0f, // Bottom side

                -0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f, -0.8f, 0.5f, 0.0f, // Left Side
                -0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f, -0.8f, 0.5f, 0.0f, // Left Side
                0.0f, 0.8f, 0.0f, 0.92f, 0.86f, 0.76f, 2.5f, 5.0f, -0.8f, 0.5f, 0.0f, // Left Side

                -0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f, 0.0f, 0.5f, -0.8f,
                // Non-facing side
                0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f, 0.0f, 0.5f, -0.8f,
                // Non-facing side
                0.0f, 0.8f, 0.0f, 0.92f, 0.86f, 0.76f, 2.5f, 5.0f, 0.0f, 0.5f, -0.8f,
                // Non-facing side

                0.5f, 0.0f, -0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f, 0.8f, 0.5f, 0.0f, // Right side
                0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f, 0.8f, 0.5f, 0.0f, // Right side
                0.0f, 0.8f, 0.0f, 0.92f, 0.86f, 0.76f, 2.5f, 5.0f, 0.8f, 0.5f, 0.0f, // Right side

                0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 5.0f, 0.0f, 0.0f, 0.5f, 0.8f, // Facing side
                -0.5f, 0.0f, 0.5f, 0.83f, 0.70f, 0.44f, 0.0f, 0.0f, 0.0f, 0.5f, 0.8f, // Facing side
                0.0f, 0.8f, 0.0f, 0.92f, 0.86f, 0.76f, 2.5f, 5.0f, 0.0f, 0.5f, 0.8f  // Facing side
            };

        indices = new int[]{
            0, 1, 2, // Bottom side
            0, 2, 3, // Bottom side
            4, 6, 5, // Left side
            7, 9, 8, // Non-facing side
            10, 12, 11, // Right side
            13, 15, 14 // Facing side
        };

        var lightVertices = new float[]
            { //     COORDINATES     //
                -0.1f, -0.1f, 0.1f,
                -0.1f, -0.1f, -0.1f,
                0.1f, -0.1f, -0.1f,
                0.1f, -0.1f, 0.1f,
                -0.1f, 0.1f, 0.1f,
                -0.1f, 0.1f, -0.1f,
                0.1f, 0.1f, -0.1f,
                0.1f, 0.1f, 0.1f
            };

        var lightIndices = new int[]{
            0, 1, 2,
            0, 2, 3,
            0, 4, 7,
            0, 7, 3,
            3, 7, 6,
            3, 6, 2,
            2, 6, 5,
            2, 5, 1,
            1, 5, 4,
            1, 4, 0,
            4, 5, 6,
            4, 6, 7

        };

        vao = new VertexArrayObject();
        vao.bind();
        vbo = new VertexBufferObject(vertices);
        ebo = new ElementBufferObject(indices);

        vao.linkAttributes(vbo, 0, 3, GL_FLOAT, 11 * 4, 0);
        vao.linkAttributes(vbo, 1, 3, GL_FLOAT, 11 * 4, 3 * 4);
        vao.linkAttributes(vbo, 2, 2, GL_FLOAT, 11 * 4, 6 * 4);
        vao.linkAttributes(vbo, 3, 3, GL_FLOAT, 11 * 4, 8 * 4);

        vao.unbind();
        vbo.unbind();
        ebo.unbind();

        // Shader for light cube
        lightVao = new VertexArrayObject();
        lightVao.bind();
        lightVbo = new VertexBufferObject(lightVertices);
        lightEbo = new ElementBufferObject(lightIndices);
        lightVao.linkAttributes(lightVbo, 0, 3, GL_FLOAT, 3 * 4, 0);
        lightVao.unbind();
        lightVbo.unbind();
        lightEbo.unbind();

        var lightColor = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        var lightPos = new Vector3f(1.5f, 0.5f, 0.5f);

        var lightModelBB = BufferUtils.createFloatBuffer(16);
        var lightModel = new Matrix4f().translate(lightPos);
        lightModel.get(lightModelBB);

        var pyramidModelBB = BufferUtils.createFloatBuffer(16);
        var pyramidPos = new Vector3f(0.0f, 0.0f, 0.0f);
        var pyramidModel = new Matrix4f().translate(pyramidPos);
        pyramidModel.get(pyramidModelBB);

        lightShader.activate();
        glUniformMatrix4fv(glGetUniformLocation(lightShader.shaderProgram(), "model"), false,
            lightModelBB);
        glUniform4f(glGetUniformLocation(lightShader.shaderProgram(), "lightColor"), lightColor.x,
            lightColor.y, lightColor.z, lightColor.w);

        shader.activate();
        glUniformMatrix4fv(glGetUniformLocation(shader.shaderProgram(), "model"), false,
            pyramidModelBB);
        glUniform4f(glGetUniformLocation(shader.shaderProgram(), "lightColor"), lightColor.x,
            lightColor.y, lightColor.z, lightColor.w);
        glUniform3f(glGetUniformLocation(shader.shaderProgram(), "lightPos"), lightPos.x,
            lightPos.y, lightPos.z);

        lightVao.bind();
        glDrawElements(GL_TRIANGLES, lightIndices.length, GL_UNSIGNED_INT, 0);

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

        // Set uniforms
        var camMatrix = camera
            .matrixBuffer(45f, 0.1f, 100f);
//            .rotateY((float) Math.toRadians(rotation));
        shader.activate();
        var cameraPosition = camera.getPosition();
        glUniform3f(glGetUniformLocation(shader.shaderProgram(), "camPos"), cameraPosition.x,
            cameraPosition.y, cameraPosition.z);
        var camMatrixLoc = glGetUniformLocation(shader.shaderProgram(), "camMatrix");
        glUniformMatrix4fv(camMatrixLoc, false, camMatrix);

        // Bind texture and VAO
        texture.bind();
        vao.bind();

        // Draw elements
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        lightShader.activate();
        glUniformMatrix4fv(glGetUniformLocation(lightShader.shaderProgram(), "camMatrix"),
            false, camMatrix);
        lightVao.bind();
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
        lightVao.delete();
        lightVbo.delete();
        lightEbo.delete();
        lightShader.delete();
    }
}
