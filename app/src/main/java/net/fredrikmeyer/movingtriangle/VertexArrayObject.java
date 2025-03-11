package net.fredrikmeyer.movingtriangle;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        id = glGenVertexArrays();
    }

    /**
     * Links a specified VertexBufferObject (VBO) to a layout index in the Vertex Array Object (VAO)
     * for attribute configuration.
     *
     * @param vbo    The VertexBufferObject to be linked.
     * @param layout The layout index to which the vertex attribute is bound.
     */
    public void link(VertexBufferObject vbo, int layout) {
        vbo.bind();
        glVertexAttribPointer(layout, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(layout);
        vbo.unbind();
    }

    /**
     * Configures and links a VertexBufferObject (VBO) to the specified layout index in the Vertex
     * Array Object (VAO) with detailed attribute settings.
     *
     * @param vbo           The VertexBufferObject to be linked.
     * @param layout        The layout index to which the vertex attribute is bound.
     * @param numComponents The number of components per vertex attribute (e.g., 2 for vec2, 3 for
     *                      vec3).
     * @param type          The data type of each component in the attribute (e.g., GL_FLOAT,
     *                      GL_INT).
     * @param stride        The byte offset between consecutive vertex attributes.
     * @param offset        The byte offset of the first component of the first vertex attribute in
     *                      the buffer.
     */
    public void linkAttributes(VertexBufferObject vbo, int layout, int numComponents, int type,
        int stride, int offset) {

        vbo.bind();
        glVertexAttribPointer(layout, numComponents, type, false, stride, offset);
        glEnableVertexAttribArray(layout);
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }
}
