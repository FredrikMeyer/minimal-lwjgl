package net.fredrikmeyer.opengl;

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

    @Test
    void testZoom() {
        // Test initial zoom
        assertEquals(1.0f, camera.getZoom(), DELTA);

        // Test zoom in
        camera.zoom(0.5f);
        assertEquals(1.5f, camera.getZoom(), DELTA);

        // Test zoom out
        camera.zoom(-0.7f);
        assertEquals(0.8f, camera.getZoom(), DELTA);

        // Test minimum zoom
        camera.zoom(-1.0f);
        assertEquals(0.1f, camera.getZoom(), DELTA, "Zoom should not go below minimum value");
    }

    @Test
    void testReset() {
        // Move camera, rotate view, and zoom
        camera.moveForward(1.0f);
        camera.moveRight(1.0f);
        camera.rotateHorizontal(0.5f);
        camera.rotateVertical(0.3f);
        camera.zoom(0.5f);

        // Verify camera moved, rotated, and zoomed
        Vector3f position = camera.getPosition();
        assertEquals(1.0f, position.x, DELTA);
        assertEquals(0, position.y, DELTA);
        assertEquals(-1.0f, position.z, DELTA);

        Vector3f orientation = camera.getOrientation();
        assertNotEquals(0, orientation.x, DELTA);
        assertNotEquals(0, orientation.y, DELTA);
        assertNotEquals(-1, orientation.z, DELTA);

        assertEquals(1.5f, camera.getZoom(), DELTA);

        // Reset camera
        camera.reset();

        // Verify camera reset to initial position, orientation, and zoom
        Vector3f resetPosition = camera.getPosition();
        assertEquals(0, resetPosition.x, DELTA);
        assertEquals(0, resetPosition.y, DELTA);
        assertEquals(0, resetPosition.z, DELTA);

        Vector3f resetOrientation = camera.getOrientation();
        assertEquals(0, resetOrientation.x, DELTA);
        assertEquals(0, resetOrientation.y, DELTA);
        assertEquals(-1, resetOrientation.z, DELTA);

        assertEquals(1.0f, camera.getZoom(), DELTA);
    }

    @Test
    void testRotateHorizontal() {
        // Test initial orientation
        Vector3f initialOrientation = camera.getOrientation();
        assertEquals(0, initialOrientation.x, DELTA);
        assertEquals(0, initialOrientation.y, DELTA);
        assertEquals(-1, initialOrientation.z, DELTA);

        // Rotate horizontally (positive angle)
        camera.rotateHorizontal(0.5f);
        Vector3f rotatedOrientation = camera.getOrientation();
        assertTrue(rotatedOrientation.x < 0, "X component should be negative after rotating right");
        assertEquals(0, rotatedOrientation.y, DELTA, "Y component should not change");
        assertTrue(rotatedOrientation.z < 0, "Z component should remain negative");

        // Rotate horizontally (negative angle)
        camera.reset();
        camera.rotateHorizontal(-0.5f);
        rotatedOrientation = camera.getOrientation();
        assertTrue(rotatedOrientation.x > 0, "X component should be positive after rotating left");
        assertEquals(0, rotatedOrientation.y, DELTA, "Y component should not change");
        assertTrue(rotatedOrientation.z < 0, "Z component should remain negative");
    }

    @Test
    void testRotateVertical() {
        // Test initial orientation
        Vector3f initialOrientation = camera.getOrientation();
        assertEquals(0, initialOrientation.x, DELTA);
        assertEquals(0, initialOrientation.y, DELTA);
        assertEquals(-1, initialOrientation.z, DELTA);

        // Rotate vertically (positive angle)
        camera.rotateVertical(0.5f);
        Vector3f rotatedOrientation = camera.getOrientation();
        assertEquals(0, rotatedOrientation.x, DELTA, "X component should not change");
        assertTrue(rotatedOrientation.y < 0, "Y component should be negative after rotating down");
        assertTrue(rotatedOrientation.z < 0, "Z component should remain negative");

        // Rotate vertically (negative angle)
        camera.reset();
        camera.rotateVertical(-0.5f);
        rotatedOrientation = camera.getOrientation();
        assertEquals(0, rotatedOrientation.x, DELTA, "X component should not change");
        assertTrue(rotatedOrientation.y > 0, "Y component should be positive after rotating up");
        assertTrue(rotatedOrientation.z < 0, "Z component should remain negative");
    }
}
