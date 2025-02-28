package net.fredrikmeyer.simpletriangle;

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

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Shader {

    private final int shaderProgram;

    public Shader(String vertexShader, String fragmentShader) {
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

        int resultFrag = GL30.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS);
        if (resultFrag == GL20.GL_FALSE) {
            String logFrag = GL30.glGetShaderInfoLog(fragmentShaderId);
            throw new RuntimeException("Failed to compile fragment shader: " + logFrag);
        }

        int resultVert = GL30.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS);
        if (resultVert == GL20.GL_FALSE) {
            String logVert = GL30.glGetShaderInfoLog(vertexShaderId);
            throw new RuntimeException("Failed to compile vertex shader: " + logVert);
        }

        // Delete the now useless Vertex and Fragment Shader objects
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    public int shaderProgram() {
        return shaderProgram;
    }

    public void activate() {
        glUseProgram(shaderProgram);
    }

    public void delete() {
        glDeleteProgram(shaderProgram);
    }

}
