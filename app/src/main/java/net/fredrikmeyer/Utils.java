package net.fredrikmeyer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class Utils {

    static String loadResource(String resource) {
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
}
