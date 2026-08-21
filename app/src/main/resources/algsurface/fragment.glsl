#version 330 core

in vec2 fragCoord; // Input from the vertex shader
out vec4 fragColor;

uniform float uTime;
uniform vec3 uCameraPosition;
uniform vec3 uCameraOrientation;
uniform vec3 uLightPosition;
uniform int uAutoRotate; // 1 for auto-rotate enabled, 0 for disabled

const int MAX_STEPS = 100;
const float MAX_DIST = 100.0;
const float EPSILON = 0.001;

// Signed distance function for the algebraic surface: x³y+xz³+y³z+z³+7z²+5z=0
float sdAlgebraicSurface(vec3 p) {
    float x = p.x;
    float y = p.y;
    float z = p.z;

    // Calculate the algebraic expression
    float value = pow(x, 3.0) * y + x * pow(z, 3.0) + pow(y, 3.0) * z + pow(z, 3.0) + 7.0 * pow(z, 2.0) + 5.0 * z;

    // Calculate gradient for better SDF approximation
    vec3 grad;
    grad.x = 3.0 * pow(x, 2.0) * y + pow(z, 3.0);
    grad.y = pow(x, 3.0) + 3.0 * pow(y, 2.0) * z;
    grad.z = 3.0 * x * pow(z, 2.0) + pow(y, 3.0) + 3.0 * pow(z, 2.0) + 14.0 * z + 5.0;

    float gradLength = length(grad);

    // Avoid division by zero
    if (gradLength < 0.0001) {
        gradLength = 0.0001;
    }

    // Better SDF approximation using the gradient
    return value / gradLength;
}

// Scene SDF
float sceneSDF(vec3 p) {
    // Rotate the scene if auto-rotate is enabled
    vec3 q;
    if (uAutoRotate == 1) {
        // Use a fixed angle instead of time-dependent angle
        float angle = 0.0; // Fixed angle (no rotation)
        float c = cos(angle);
        float s = sin(angle);
        q = vec3(
            c * p.x + s * p.z,
            p.y,
            -s * p.x + c * p.z
        );
    } else {
        q = p; // No rotation
    }

    // Scale the scene to better show the 3D structure
    // Use a moderate scale factor to see more of the surface
    q = q * 0.8;

    return sdAlgebraicSurface(q);
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

// Ray marching with improved precision
float rayMarch(vec3 ro, vec3 rd) {
    float depth = 0.0;
    float epsilon = EPSILON;

    // Increase max steps for better surface detail
    const int IMPROVED_MAX_STEPS = 200;

    for (int i = 0; i < IMPROVED_MAX_STEPS; i++) {
        vec3 p = ro + depth * rd;

        // Adaptive precision: use smaller steps near the origin
        float distToOrigin = length(p);
        if (distToOrigin < 1.0) {
            // Near the origin, use higher precision
            epsilon = EPSILON * 0.05;
        } else {
            epsilon = EPSILON;
        }

        float dist = sceneSDF(p);

        // More conservative step size for better accuracy
        float stepSize = min(dist * 0.5, 0.1);

        // Even smaller steps near the origin or when close to the surface
        if (distToOrigin < 1.0 || abs(dist) < 0.1) {
            stepSize = max(abs(dist) * 0.2, epsilon); // Smaller steps for better precision
        }

        depth += stepSize;

        if (abs(dist) < epsilon || depth > MAX_DIST) break;
    }

    return depth;
}

// Phong shading
vec3 phongShading(vec3 p, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(uLightPosition - p);

    // Calculate distance to origin for special coloring
    float distToOrigin = length(p);

    // Material properties - color based on distance to origin
    // Use a hot color scheme (red/yellow) near the origin to highlight the singularity
    // and transition to cooler colors (blue) further away
    vec3 objectColor;
    if (distToOrigin < 0.1) {
        // Very close to origin (singularity) - bright red/orange
        objectColor = vec3(1.0, 0.3, 0.0);
    } else if (distToOrigin < 0.3) {
        // Near origin - yellow to green transition
        float t = (distToOrigin - 0.1) / 0.2; // Normalized 0-1 in this range
        objectColor = mix(vec3(1.0, 0.3, 0.0), vec3(0.2, 0.8, 0.2), t);
    } else {
        // Further from origin - transition to blue
        float t = min((distToOrigin - 0.3) / 0.7, 1.0); // Normalized 0-1 in this range
        objectColor = mix(vec3(0.2, 0.8, 0.2), vec3(0.2, 0.5, 0.7), t);
    }

    float ambient = 0.1;
    float diffuse = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);

    // Enhance specular highlight near the origin to emphasize the singularity
    if (distToOrigin < 0.2) {
        specular *= 2.0;
    }

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

        // Allow plotting in a larger region to see more of the surface
        if (abs(p.x) > 3.0 || abs(p.y) > 3.0 || abs(p.z) > 3.0) {
            // Sky color
            vec3 skyColor = vec3(0.1, 0.2, 0.3);
            fragColor = vec4(skyColor, 1.0);
            return;
        }

        // Calculate normal
        vec3 normal = calcNormal(p);

        // Shading
        vec3 color = phongShading(p, normal, -rd);

        // Special highlight for points very close to the origin (the singularity)
        float distToOrigin = length(p);
        if (distToOrigin < 0.05) {
            // Add a bright highlight to emphasize the singularity
            float highlightIntensity = 1.0 - distToOrigin / 0.05; // 1.0 at origin, 0.0 at distance 0.05
            color = mix(color, vec3(1.0, 1.0, 1.0), highlightIntensity * 0.7); // Mix with white
        }

        fragColor = vec4(color, 1.0);
    } else {
        // Sky color
        vec3 skyColor = vec3(0.1, 0.2, 0.3);
        fragColor = vec4(skyColor, 1.0);
    }
}
