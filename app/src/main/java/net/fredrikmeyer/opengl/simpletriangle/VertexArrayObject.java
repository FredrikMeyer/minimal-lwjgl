package net.fredrikmeyer.opengl.simpletriangle;

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
     * Links a specified VertexBufferObject (VBO) to a layout index in the
     * Vertex Array Object (VAO) for attribute configuration.
     *
     * @param vbo The VertexBufferObject to be linked.
     * @param layout The layout index to which the vertex attribute is bound.
     */
    public void link(VertexBufferObject vbo, int layout) {
        vbo.bind();
        glVertexAttribPointer(layout, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(layout);
        vbo.unbind();
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
