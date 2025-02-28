#version 330 core

precision mediump float;

in vec3 vNormal;
out vec4 fragColor;

void main() {
    vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));
    float light = max(dot(vNormal, lightDir), 0.2);
    fragColor = vec4(light, light, light, 1.0);
}