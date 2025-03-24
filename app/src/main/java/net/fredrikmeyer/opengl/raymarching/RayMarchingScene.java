package net.fredrikmeyer.opengl.raymarching;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;

import net.fredrikmeyer.opengl.Camera;
import net.fredrikmeyer.opengl.ElementBufferObject;
import net.fredrikmeyer.opengl.IScene;
import net.fredrikmeyer.opengl.Shader;
import net.fredrikmeyer.opengl.Utils;
import net.fredrikmeyer.opengl.VertexArrayObject;
import net.fredrikmeyer.opengl.VertexBufferObject;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * A scene that renders a 3D hexagonal prism using ray marching with signed distance functions.
 */
public class RayMarchingScene implements IScene {

    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private ElementBufferObject ebo;
    private Shader shader;
    private int[] quadIndices;
    private int uTimeId;
    private int uCameraPositionId;
    private int uCameraOrientationId;
    private int uLightPositionId;
    private Camera camera;

    /**
     * Constructs a new RayMarchingScene instance, initializing the shader and geometry for the scene.
     * 
     * @param camera the camera to use for rendering
     */
    public RayMarchingScene(Camera camera) {
        this.camera = camera;

        // Load shader
        shader = new Shader(
            Utils.loadResource("raymarching/vertex.glsl"),
            Utils.loadResource("raymarching/fragment.glsl"));

        // Create geometry
        createGeometry();

        // Get uniform locations
        uTimeId = glGetUniformLocation(shader.shaderProgram(), "uTime");
        uCameraPositionId = glGetUniformLocation(shader.shaderProgram(), "uCameraPosition");
        uCameraOrientationId = glGetUniformLocation(shader.shaderProgram(), "uCameraOrientation");
        uLightPositionId = glGetUniformLocation(shader.shaderProgram(), "uLightPosition");
    }

    /**
     * Creates the geometry for the scene (a full-screen quad).
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
        // No updates needed for this scene
    }

    @Override
    public void render() {
        shader.activate();

        // Set time uniform for animation
        float time = (float) GLFW.glfwGetTime();
        glUniform1f(uTimeId, time);

        // Set camera position from the Camera object
        Vector3f cameraPosition = camera.getPosition();
        glUniform3f(uCameraPositionId, cameraPosition.x, cameraPosition.y, cameraPosition.z);

        // Set camera orientation from the Camera object
        Vector3f cameraOrientation = camera.getOrientation();
        glUniform3f(uCameraOrientationId, cameraOrientation.x, cameraOrientation.y, cameraOrientation.z);

        // Set light position
        float lightX = (float) (Math.sin(time) * 3.0);
        float lightZ = (float) (Math.cos(time) * 3.0);
        glUniform3f(uLightPositionId, lightX, 5.0f, lightZ);

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
