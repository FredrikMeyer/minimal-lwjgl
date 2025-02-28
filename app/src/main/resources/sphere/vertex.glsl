#version 330 core

layout (location = 0) in vec3 inPosition; // Attribute 0: Positions
layout (location = 1) in vec3 aNormal;   // Attribute 1: Normals

uniform mat4 uModelViewMatrix;
uniform mat4 uProjectionMatrix;

out vec3 vNormal;

void main() {
    gl_Position = uProjectionMatrix * uModelViewMatrix * vec4(inPosition, 1.0);
    vNormal = aNormal;
}