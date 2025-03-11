#version 330

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 aColor;
layout (location = 2) in vec2 aTex;

// Outputs the color for the Fragment Shader
out vec3 color;
// Outputs the texture coordinates to the fragment shader
out vec2 texCoord;

// Controls the scale of the vertices
uniform float scale;

void main()
{
    gl_Position = vec4(inPosition * scale, 1.0);
    color = aColor;
    texCoord = aTex;
}