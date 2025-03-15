package net.fredrikmeyer;

public record WindowDimensions(int width, int height) {
    public static WindowDimensions of(int width, int height) {
        return new WindowDimensions(width, height);
    }
}
