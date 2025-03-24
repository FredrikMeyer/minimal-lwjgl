#version 330 core

in vec2 fragCoord; // Input from the vertex shader
out vec4 fragColor;

uniform float uTime;
uniform vec3 uCameraPosition;
uniform vec3 uCameraOrientation;
uniform vec3 uLightPosition;

const int MAX_STEPS = 100;
const float MAX_DIST = 100.0;
const float EPSILON = 0.001;

float sdCutHollowSphere( vec3 p, float r, float h, float t )
{
    // sampling independent computations (only depend on shape)
    float w = sqrt(r*r-h*h);

    // sampling dependant computations
    vec2 q = vec2( length(p.xz), p.y );
    return ((h*q.x<w*q.y) ? length(q-vec2(w,h)) :
    abs(length(q)-r) ) - t;
}

// Signed distance function for a hexagonal prism
float sdHexPrism(vec3 p, vec2 h) {
    const vec3 k = vec3(-0.8660254, 0.5, 0.57735);
    p = abs(p);
    p.xy -= 2.0 * min(dot(k.xy, p.xy), 0.0) * k.xy;
    vec2 d = vec2(
        length(p.xy - vec2(clamp(p.x, -k.z * h.x, k.z * h.x), h.x)) * sign(p.y - h.x),
        p.z - h.y);
    return min(max(d.x, d.y), 0.0) + length(max(d, 0.0));
}

// Scene SDF
float sceneSDF(vec3 p) {
    // Rotate the scene
    float angle = uTime * 0.5;
    float c = cos(angle);
    float s = sin(angle);
    vec3 q = vec3(
        c * p.x + s * p.z,
        p.y,
        -s * p.x + c * p.z
    );

    // Hexagonal prism
    vec2 h = vec2(1.0, 0.5); // Radius and half-height
    return sdCutHollowSphere(q, 1.0, 0.5, 0.2);
//    return sdHexPrism(q, h);
}

// Calculate normal at a point
vec3 calcNormal(vec3 p) {
    const float h = 0.0001;
    const vec2 k = vec2(1, -1);
    return normalize(
        k.xyy * sceneSDF(p + k.xyy * h) +
        k.yxy * sceneSDF(p + k.yxy * h) +
        k.yyx * sceneSDF(p + k.yyx * h) +
        k.xxx * sceneSDF(p + k.xxx * h)
    );
}

// Ray marching
float rayMarch(vec3 ro, vec3 rd) {
    float depth = 0.0;

    for (int i = 0; i < MAX_STEPS; i++) {
        vec3 p = ro + depth * rd;
        float dist = sceneSDF(p);
        depth += dist;
        if (dist < EPSILON || depth > MAX_DIST) break;
    }

    return depth;
}

// Phong shading
vec3 phongShading(vec3 p, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(uLightPosition - p);

    // Material properties
    vec3 objectColor = vec3(0.7, 0.2, 0.3); // Reddish color
    float ambient = 0.1;
    float diffuse = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);

    // Calculate shadows
    float shadow = 1.0;
    float distToLight = length(uLightPosition - p);
    vec3 dirToLight = normalize(uLightPosition - p);

    // March from point towards light
    float t = 0.02; // Start a bit away from the surface
    for (int i = 0; i < 32; i++) {
        float h = sceneSDF(p + dirToLight * t);
        if (h < 0.001) {
            shadow = 0.1;
            break;
        }
        if (t >= distToLight) break;
        t += h;
    }

    return (ambient + shadow * (diffuse + 0.5 * specular)) * objectColor;
}

void main() {
    // Screen coordinates
    vec2 uv = fragCoord;

    // Camera setup
    vec3 ro = uCameraPosition; // Ray origin (camera position)
    vec3 rd = normalize(vec3(uv, 1.0)); // Ray direction

    // Use camera orientation
    vec3 forward = uCameraOrientation;
    vec3 up = vec3(0.0, 1.0, 0.0);
    vec3 right = normalize(cross(forward, up));
    vec3 cameraUp = normalize(cross(right, forward));

    rd = normalize(uv.x * right + uv.y * cameraUp + 1.5 * forward);

    // Ray marching
    float dist = rayMarch(ro, rd);

    if (dist < MAX_DIST) {
        // Hit point
        vec3 p = ro + dist * rd;

        // Calculate normal
        vec3 normal = calcNormal(p);

        // Shading
        vec3 color = phongShading(p, normal, -rd);

        fragColor = vec4(color, 1.0);
    } else {
        // Sky color
        vec3 skyColor = vec3(0.1, 0.2, 0.3);
        fragColor = vec4(skyColor, 1.0);
    }
}
