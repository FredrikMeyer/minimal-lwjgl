package net.fredrikmeyer.opengl.raymarching;

import net.fredrikmeyer.opengl.Camera;
import net.fredrikmeyer.opengl.InputHandler;
import net.fredrikmeyer.opengl.ScreenshotManager;
import net.fredrikmeyer.opengl.Window;

/**
 * Custom InputHandler for the RayMarchingApp that adds support for toggling auto-rotation with the Z key.
 */
public class RayMarchingInputHandler extends InputHandler {
    
    private final RayMarchingScene rayMarchingScene;
    
    /**
     * Creates a new RayMarchingInputHandler for the specified window.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     * @param rayMarchingScene  the ray marching scene
     */
    public RayMarchingInputHandler(Window window, ScreenshotManager screenshotManager, Camera camera, RayMarchingScene rayMarchingScene) {
        super(window, screenshotManager, camera);
        this.rayMarchingScene = rayMarchingScene;
    }
    
    @Override
    protected void onZKeyPressed() {
        boolean isEnabled = rayMarchingScene.toggleAutoRotate();
        System.out.println("Auto-rotation " + (isEnabled ? "enabled" : "disabled"));
    }
}