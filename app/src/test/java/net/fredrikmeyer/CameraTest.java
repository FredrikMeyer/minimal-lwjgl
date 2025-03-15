package net.fredrikmeyer;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    private Camera camera;
    private static final float DELTA = 0.0001f;

    @BeforeEach
    void setUp() {
        // Create a camera at position (0, 0, 0) with aspect ratio 1.0
        camera = new Camera(1.0f, new Vector3f(0, 0, 0));
    }

    @Test
    void testInitialPosition() {
        Vector3f position = camera.getPosition();
        assertEquals(0, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(0, position.z, DELTA);
    }

    @Test
    void testMoveForward() {
        // Moving forward should decrease Z (in OpenGL, -Z is forward)
        camera.moveForward(1.0f);
        Vector3f position = camera.getPosition();
        assertEquals(0, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(-1.0f, position.z, DELTA);
    }

    @Test
    void testMoveBackward() {
        // Moving backward should increase Z
        camera.moveBackward(1.0f);
        Vector3f position = camera.getPosition();
        assertEquals(0, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(1.0f, position.z, DELTA);
    }

    @Test
    void testMoveLeft() {
        // Moving left should decrease X
        camera.moveLeft(1.0f);
        Vector3f position = camera.getPosition();
        assertEquals(-1.0f, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(0, position.z, DELTA);
    }

    @Test
    void testMoveRight() {
        // Moving right should increase X
        camera.moveRight(1.0f);
        Vector3f position = camera.getPosition();
        assertEquals(1.0f, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(0, position.z, DELTA);
    }

    @Test
    void testMultipleMovements() {
        // Test a sequence of movements
        camera.moveForward(0.5f);
        camera.moveRight(0.5f);
        camera.moveBackward(0.2f);
        camera.moveLeft(0.1f);
        
        Vector3f position = camera.getPosition();
        assertEquals(0.4f, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(-0.3f, position.z, DELTA);
    }
}