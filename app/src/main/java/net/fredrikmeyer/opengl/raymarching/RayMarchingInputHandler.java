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
     * Creates a new RayMarchingInputHandler for the specified window and registers its input callbacks.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     * @param rayMarchingScene  the ray marching scene
     * @return a fully initialized RayMarchingInputHandler
     */
    public static RayMarchingInputHandler create(Window window, ScreenshotManager screenshotManager, Camera camera, RayMarchingScene rayMarchingScene) {
        RayMarchingInputHandler handler = new RayMarchingInputHandler(window, screenshotManager, camera, rayMarchingScene);
        handler.installCallbacks();
        return handler;
    }

    private RayMarchingInputHandler(Window window, ScreenshotManager screenshotManager, Camera camera, RayMarchingScene rayMarchingScene) {
        super(window, screenshotManager, camera);
        this.rayMarchingScene = rayMarchingScene;
    }
    
    @Override
    protected void onZKeyPressed() {
        boolean isEnabled = rayMarchingScene.toggleAutoRotate();
        System.out.println("Auto-rotation " + (isEnabled ? "enabled" : "disabled"));
    }
}