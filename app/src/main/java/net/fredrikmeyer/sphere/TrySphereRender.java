package net.fredrikmeyer.sphere;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.Objects;
import net.fredrikmeyer.ElementBufferObject;
import net.fredrikmeyer.Shader;
import net.fredrikmeyer.Utils;
import net.fredrikmeyer.VertexArrayObject;
import net.fredrikmeyer.VertexBufferObject;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class TrySphereRender {

    private long window;
    private VertexBufferObject vboId;
    private Shader shader;
    private VertexArrayObject vao;
    private ElementBufferObject ebo;
    private VertexBufferObject vboNormalsId;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        clean();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (window == NULL) {
            glfwTerminate(); // ?
            throw new RuntimeException("Failed to create the GLFW window");
        }

        setKeyCallback();

        // Get the thread stack and push a new frame
        centerWindow();

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    record Sphere(float[] vertices, float[] normals, int[] indices) {

    }

    Sphere generateSphere(int n) {
        // Latitude and longitude bands (increase for smoother spheres)
        int latBands = n;
        int longBands = n;

        // Calculate the total number of vertices
        int totalVertices = (latBands + 1) * (longBands + 1);
        int totalIndices = latBands * longBands * 6;

        // Allocate arrays with proper sizes
        float[] vertices = new float[totalVertices * 3]; // each vertex has x, y, z
        float[] normals = new float[totalVertices * 3]; // each vertex has a normal (x, y, z)
        int[] indices = new int[totalIndices];          // each face has 6 indices

        int vertexIndex = 0;
        int normalIndex = 0;
        int indexIndex = 0;
        float radius = 1.0f; // Adjust as needed

        for (int lat = 0; lat <= latBands; lat++) {
            double theta = (lat * Math.PI) / latBands; // Latitude
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int lon = 0; lon <= longBands; lon++) {
                double phi = (lon * 2 * Math.PI) / longBands; // Longitude
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;

                // Normal vector
                normals[normalIndex++] = x;
                normals[normalIndex++] = y;
                normals[normalIndex++] = z;

                // Vertex position
                vertices[vertexIndex++] = radius * x;
                vertices[vertexIndex++] = radius * y;
                vertices[vertexIndex++] = radius * z;
            }
        }

        for (int lat = 0; lat < latBands; lat++) {
            for (int lon = 0; lon < longBands; lon++) {
                int first = lat * (longBands + 1) + lon;
                int second = first + longBands + 1;

                // Triangle 1
                indices[indexIndex++] = first;
                indices[indexIndex++] = second;
                indices[indexIndex++] = first + 1;

                // Triangle 2
                indices[indexIndex++] = second;
                indices[indexIndex++] = second + 1;
                indices[indexIndex++] = first + 1;
            }
        }
        return new Sphere(vertices, normals, indices);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        var caps = GL.createCapabilities();
        System.out.println(caps.forwardCompatible);

        shader = new Shader(
            Utils.loadResource("sphere/vertex.glsl"),
            Utils.loadResource("sphere/fragment.glsl"));

        var sphere = generateSphere(100);

        vao = new VertexArrayObject();
        vao.bind();
        vboId = new VertexBufferObject(sphere.vertices);
        ebo = new ElementBufferObject(sphere.indices);

        vboNormalsId = new VertexBufferObject(sphere.normals);
        vboNormalsId.bind();

        vao.link(vboId, 0);
        vao.link(vboNormalsId, 1);

        vao.unbind();
        vboId.unbind();
        ebo.unbind();

        var mv = new Matrix4f().translate(0, 0, -4);
        var pm = new Matrix4f().perspective((float) (Math.PI / 4), 1.0F, 0.1F, 100);

        var mvLoc = glGetUniformLocation(shader.shaderProgram(), "uModelViewMatrix");
        var pmLoc = glGetUniformLocation(shader.shaderProgram(), "uProjectionMatrix");

        glUniformMatrix4fv(mvLoc, false, mv.get(new float[16]));
        glUniformMatrix4fv(pmLoc, false, pm.get(new float[16]));


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Clear the screen
            glClearColor(0.07f, 0.13f, 0.17f, 1.0f);
            // Clean the back buffer and assign the new color to it
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.activate();
            vao.bind();

//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDrawElements(GL_TRIANGLES, sphere.indices.length, GL_UNSIGNED_INT, 0);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }

    private void clean() {
        vao.delete();
        vboId.delete();
        ebo.delete();
        shader.delete();
    }

    public static void main(String[] args) {
        new TrySphereRender().run();
    }

    private void setKeyCallback() {
        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        //noinspection resource
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });
    }

    private void centerWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }
}
