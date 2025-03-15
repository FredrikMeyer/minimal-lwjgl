package net.fredrikmeyer;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Camera {

    private static final Vector3f UP = new Vector3f(0, 1, 0);

    private final Vector3f position;
    private final Vector3f initialPosition;
    private final float aspectRatio;
    private float zoom = 1.0f;

    // Camera orientation
    private final Vector3f orientation = new Vector3f(0, 0, -1);
    private final Vector3f initialOrientation = new Vector3f(0, 0, -1);

    /**
     * Gets the current position of the camera.
     *
     * @return a copy of the camera's position vector
     */
    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    /**
     * Gets the current zoom level of the camera.
     *
     * @return the zoom level
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Gets the current orientation of the camera.
     *
     * @return a copy of the camera's orientation vector
     */
    public Vector3f getOrientation() {
        return new Vector3f(orientation);
    }

    public Camera(float aspectRatio, Vector3f position) {
        this.aspectRatio = aspectRatio;
        this.position = position;
        this.initialPosition = new Vector3f(position);
    }

    Matrix4f matrix(float fovDeg, float nearPlane, float farPlane) {
        var view = new Matrix4f().lookAt(position, position.add(orientation, new Vector3f()),
            UP);

        // Apply zoom by adjusting the field of view
        float zoomedFov = fovDeg / zoom;
        var projection = new Matrix4f().perspective(zoomedFov, aspectRatio, nearPlane,
            farPlane);

        return projection.mul(view);
    }

    FloatBuffer matrixBuffer(float fovDeg, float nearPlane, float farPlane) {
        var fb = BufferUtils.createFloatBuffer(16);
        matrix(fovDeg, nearPlane, farPlane).get(fb);
        return fb;
    }

    /**
     * Zooms the camera in or out.
     *
     * @param amount the amount to zoom (positive for zoom in, negative for zoom out)
     */
    public void zoom(float amount) {
        zoom += amount;
        // Ensure zoom doesn't go below a minimum value to prevent issues
        if (zoom < 0.1f) {
            zoom = 0.1f;
        }
    }

    /**
     * Resets the camera to its initial position, orientation, and zoom.
     */
    public void reset() {
        position.set(initialPosition);
        orientation.set(initialOrientation);
        zoom = 1.0f;
    }

    /**
     * Rotates the camera view horizontally (yaw).
     *
     * @param angle the angle to rotate in radians
     */
    public void rotateHorizontal(float angle) {
        // Rotate around the UP vector
        Vector3f rotated = new Vector3f(orientation);
        rotated.rotateY(angle);
        orientation.set(rotated);
        orientation.normalize(); // Ensure the orientation vector stays normalized
    }



    /**
     * Rotates the camera view vertically (pitch).
     *
     * @param angle the angle to rotate in radians
     */
    public void rotateVertical(float angle) {
        // Rotate around the right vector (cross product of orientation and UP)
        Vector3f right = orientation.cross(UP, new Vector3f()).normalize();
        Vector3f rotated = new Vector3f(orientation);
        // Negate the angle to match expected behavior (positive angle = look down)
        rotated.rotateAxis(-angle, right.x, right.y, right.z);
        orientation.set(rotated);
        orientation.normalize(); // Ensure the orientation vector stays normalized
    }

    void moveForward(float amount) {
        position.add(orientation.mul(amount, new Vector3f()));
    }

    void moveBackward(float amount) {
        position.sub(orientation.mul(amount, new Vector3f()));
    }

    void moveLeft(float amount) {
        position.sub(orientation.cross(UP, new Vector3f()).mul(amount, new Vector3f()));
    }

    void moveRight(float amount) {
        position.add(orientation.cross(UP, new Vector3f()).mul(amount, new Vector3f()));
    }
}
