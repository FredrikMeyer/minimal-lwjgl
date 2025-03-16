package net.fredrikmeyer.opengl;

public record WindowDimensions(int width, int height) {
    public static WindowDimensions of(int width, int height) {
        return new WindowDimensions(width, height);
    }
}
