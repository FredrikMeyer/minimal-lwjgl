package net.fredrikmeyer.opengl;

import java.nio.ByteBuffer;

/**
 * Responsible for loading resources such as shaders and textures.
 */
public class ResourceLoader {
    
    /**
     * Loads a text resource from the classpath.
     *
     * @param resourcePath the path to the resource
     * @return the content of the resource as a string
     */
    public String loadTextResource(String resourcePath) {
        return Utils.loadResource(resourcePath);
    }
    
    /**
     * Loads a binary resource from the classpath into a ByteBuffer.
     *
     * @param resourcePath the path to the resource
     * @return the content of the resource as a ByteBuffer
     */
    public ByteBuffer loadBinaryResource(String resourcePath) {
        return Utils.loadResourceByteBuffer(resourcePath);
    }
    
    /**
     * Loads a shader from the classpath.
     *
     * @param vertexShaderPath the path to the vertex shader
     * @param fragmentShaderPath the path to the fragment shader
     * @return a new Shader object
     */
    public Shader loadShader(String vertexShaderPath, String fragmentShaderPath) {
        String vertexShaderSource = loadTextResource(vertexShaderPath);
        String fragmentShaderSource = loadTextResource(fragmentShaderPath);
        return new Shader(vertexShaderSource, fragmentShaderSource);
    }
    
    /**
     * Loads a texture from the classpath.
     *
     * @param texturePath the path to the texture
     * @return a new Texture object
     */
    public Texture loadTexture(String texturePath) {
        ByteBuffer textureBuffer = loadBinaryResource(texturePath);
        return new Texture(textureBuffer);
    }
}