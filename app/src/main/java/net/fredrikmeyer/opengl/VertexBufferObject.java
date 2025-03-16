package net.fredrikmeyer.opengl;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL15C.glGenBuffers;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class VertexBufferObject {

    private final int vboId;

    private final FloatBuffer verticesBuffer;

    /**
     * Constructs a new VertexBufferObject and initializes it with the given vertex data.
     * The vertex data is uploaded to the GPU as a static draw buffer.
     *
     * @param vertices an array of float values representing the vertex data
     */
    public VertexBufferObject(float[] vertices) {
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void delete() {
        MemoryUtil.memFree(verticesBuffer);
        glDeleteBuffers(vboId);
    }
}
