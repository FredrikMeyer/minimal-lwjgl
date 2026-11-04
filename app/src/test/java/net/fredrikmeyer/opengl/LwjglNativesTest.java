package net.fredrikmeyer.opengl;

import org.junit.jupiter.api.Test;
import org.lwjgl.system.MemoryUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LwjglNativesTest {

    @Test
    void testNativeMemoryAccess() {
        var buffer = MemoryUtil.memAlloc(1);
        try {
            buffer.put(0, (byte) 42);
            assertEquals((byte) 42, buffer.get(0));
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }
}
