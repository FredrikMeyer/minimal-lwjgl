#version 330 core

in vec2 fragCoord; // Input from the vertex shader
out vec4 fragColor;

uniform vec2 uRange; // Range for x and y, e.g., (-2, 2)
uniform float uThreshold; // Approximation threshold for the curve
uniform float uParam;

void main() {
    // Map fragment coordinates from NDC (-1, 1) to the world range (e.g., -2 to 2)
    float x = mix(uRange.x, uRange.y, (fragCoord.x + 1.0) / 2.0);
    float y = mix(uRange.x, uRange.y, (fragCoord.y + 1.0) / 2.0);

    // Evaluate the algebraic equation of the curve: y^2 - x^3
    //    float curveValue = y * y - x * x * x + uParam;

    //    float curveValue = y * y - x * x * (x + 1) + uParam;

    // Lemniscate:
//    float curveValue = (x * x + y * y) * (x * x + y * y) - uParam * uParam * (x * x - y * y);
    float curveValue = ((x * x + y * y) / uParam) * ((x * x + y * y) / uParam) - (x * x - y * y);

    // Check if |curveValue| < threshold (to deal with floating-point precision)
    const float epsilon = 1e-1;
    if (abs(curveValue) < uThreshold * max(abs(curveValue), epsilon)) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Color for curve
    } else {
        discard;
    }
//
//    if (abs(curveValue) < uThreshold) {
//        fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Color for the curve (red)
//    } else {
//        discard; // Discard fragments not on the curve
//    }
}
