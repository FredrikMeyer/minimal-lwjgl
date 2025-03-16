#version 330 core

layout(location = 0) in vec2 position; // 2D positions of the quad
out vec2 fragCoord; // Pass the position to the fragment shader

void main() {
    gl_Position = vec4(position, 0.0, 1.0); // Full-screen quad in NDC
    fragCoord = position; // Pass the position as is
}
