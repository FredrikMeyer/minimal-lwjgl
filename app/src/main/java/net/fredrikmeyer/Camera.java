package net.fredrikmeyer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private static final Vector3f up = new Vector3f(0, 1, 0);
    private static final Vector3f orientation = new Vector3f(0, 0, -1);

    private final Vector3f position;
    private final float aspectRatio;

    public Camera(float aspectRatio, Vector3f position) {
        this.aspectRatio = aspectRatio;
        this.position = position;
    }

    Matrix4f matrix(float fovDeg, float nearPlane, float farPlane) {
        var view = new Matrix4f().lookAt(position, position.add(orientation, new Vector3f()),
            up);

        var projection = new Matrix4f().perspective(fovDeg, aspectRatio, nearPlane,
            farPlane);

        return projection.mul(view);
    }

    void moveForward(float amount) {
        position.add(orientation.mul(amount, new Vector3f()));
    }

    void moveBackward(float amount) {
        position.sub(orientation.mul(amount, new Vector3f()));
    }

    void moveLeft(float amount) {
        position.sub(orientation.cross(up, new Vector3f()).mul(amount, new Vector3f()));
    }

    void moveRight(float amount) {
        position.add(orientation.cross(up, new Vector3f()).mul(amount, new Vector3f()));
    }
}
