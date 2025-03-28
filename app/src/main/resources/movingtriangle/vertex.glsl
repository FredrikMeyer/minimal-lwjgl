#version 330

layout (location = 0) in vec3 inPosition;
//layout (location = 1) in vec3 aColor;

out vec3 color;
uniform float scale;

void main()
{
    gl_Position = vec4(inPosition * scale, 1.0);
    color = inPosition;
}