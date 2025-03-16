package net.fredrikmeyer.opengl;

import static org.lwjgl.system.MemoryUtil.memSlice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.Objects;
import java.util.stream.Collectors;
import org.lwjgl.BufferUtils;

public class Utils {

    public static String loadResource(String resource) {
        try (var inputStream = Utils.class.getClassLoader().getResourceAsStream(resource);
            var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + resource);
            }
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resource, e);
        }
    }


    public static ByteBuffer loadResourceByteBuffer(String resource) {
        try (var inputStream = Utils.class.getClassLoader().getResourceAsStream(resource);
            var rbc = Channels.newChannel(Objects.requireNonNull(inputStream))) {

            ByteBuffer bb = BufferUtils.createByteBuffer(8 * 1024); // ???
            while (true) {
                int bytes = rbc.read(bb);
                if (bytes == -1) {
                    break;
                }
                if (bb.remaining() == 0) {
                    bb = resizeBuffer(bb, bb.capacity() * 3 / 2); // 50%
                }
            }
            bb.flip();

            return memSlice(bb);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resource, e);
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
