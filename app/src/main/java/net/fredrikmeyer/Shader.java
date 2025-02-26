package net.fredrikmeyer;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Shader {

    private final int shaderProgram;

    Shader(String vertexShader, String fragmentShader) {
        var vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShaderId, vertexShader);
        glCompileShader(vertexShaderId);

        var fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShaderId, fragmentShader);
        glCompileShader(fragmentShaderId);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShaderId);
        glAttachShader(shaderProgram, fragmentShaderId);
        glLinkProgram(shaderProgram);

        // Delete the now useless Vertex and Fragment Shader objects
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    public void activate() {
        glUseProgram(shaderProgram);
    }

    public void delete() {
        glDeleteProgram(shaderProgram);
    }

}
