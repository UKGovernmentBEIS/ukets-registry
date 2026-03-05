package gov.uk.ets.registry.api.regulatornotice.service;

import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    private FileUtils() {
        // utility class
    }

    public static byte[] loadFileFromResources(String resourcePath) {
        try (InputStream is = FileUtils.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new IllegalArgumentException(
                        "Test resource not found: " + resourcePath
                );
            }

            return is.readAllBytes();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load test resource: " + resourcePath,
                    e
            );
        }
    }
}
