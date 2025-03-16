#version 330 core

in vec2 fragCoord; // Input from the vertex shader
out vec4 fragColor;

uniform vec2 uRange; // Range for x and y, e.g., (-2, 2)
uniform float uTime; // Approximation threshold for the curve
uniform float uParam;

#define PI 3.1415926535897932384626433832795

float colormap_red(float x) {
    if (x < 0.5) {
        return -6.0 * x + 67.0 / 32.0;
    } else {
        return 6.0 * x - 79.0 / 16.0;
    }
}

float colormap_green(float x) {
    if (x < 0.4) {
        return 6.0 * x - 3.0 / 32.0;
    } else {
        return -6.0 * x + 79.0 / 16.0;
    }
}

float colormap_blue(float x) {
    if (x < 0.7) {
        return 6.0 * x - 67.0 / 32.0;
    } else {
        return -6.0 * x + 195.0 / 32.0;
    }
}

vec4 colormap(float x) {
    float r = clamp(colormap_red(x), 0.0, 1.0);
    float g = clamp(colormap_green(x), 0.0, 1.0);
    float b = clamp(colormap_blue(x), 0.0, 1.0);
    return vec4(r, g, b, 1.0);
}



void main() {
    // Map fragment coordinates from NDC (-1, 1) to the world range (e.g., -2 to 2)
    float x = mix(uRange.x, uRange.y, (fragCoord.x + 1.0) / 2.0);
    float y = mix(uRange.x, uRange.y, (fragCoord.y + 1.0) / 2.0);

    float m = 5;
    float n = 3;

    float sinScaled = 0.5 * (sin(uTime) + 1.0);
    float L =  PI;

//    float amount = cos(n * x * L) * cos(m * y * L) - cos(m * x* L) * cos(n * y * L);
    float amount = fract((0.5 + sinScaled) * 10 * (cos(n * x * L) * cos(m * y * L) - cos(m * x* L) * cos(n * y * L)));


//    fragColor = vec4(vec3(step(sinScaled, (amount / 4.0) + 0.5)), 1.0);
    // https://paulbourke.net/geometry/chladni/
    // https://www.reddit.com/r/processing/comments/1jb5lab/having_fun_with_the_chladni_patterns/
    // https://github.com/kbinani/colormap-shaders/tree/master
//    fragColor = colormap((amount / 4.0) + 0.5);
    fragColor = colormap(amount);


//    const float epsilon = 1e-2;
//    if (abs(amount) < epsilon) {
//        fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Color for curve
//    } else {
//        discard;
//    }
}