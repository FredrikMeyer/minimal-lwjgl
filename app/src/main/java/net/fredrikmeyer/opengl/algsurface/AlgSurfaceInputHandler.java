package net.fredrikmeyer.opengl.algsurface;

import net.fredrikmeyer.opengl.Camera;
import net.fredrikmeyer.opengl.InputHandler;
import net.fredrikmeyer.opengl.ScreenshotManager;
import net.fredrikmeyer.opengl.Window;

/**
 * Custom InputHandler for the AlgSurfaceApp that adds support for toggling auto-rotation with the Z key.
 */
public class AlgSurfaceInputHandler extends InputHandler {
    
    private final AlgSurfaceScene algSurfaceScene;
    
    /**
     * Creates a new AlgSurfaceInputHandler for the specified window.
     *
     * @param window            the window to handle input for
     * @param screenshotManager the screenshot manager to use for taking screenshots
     * @param camera            the camera
     * @param algSurfaceScene   the algebraic surface scene
     */
    public AlgSurfaceInputHandler(Window window, ScreenshotManager screenshotManager, Camera camera, AlgSurfaceScene algSurfaceScene) {
        super(window, screenshotManager, camera);
        this.algSurfaceScene = algSurfaceScene;
    }
    
    @Override
    protected void onZKeyPressed() {
        boolean isEnabled = algSurfaceScene.toggleAutoRotate();
        System.out.println("Auto-rotation " + (isEnabled ? "enabled" : "disabled"));
    }
}