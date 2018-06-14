package com.github.cuzfrog.gradle.lean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestFileSystem {

    private TestFileSystem() {}

    static Path createTempDir() {
        try {
            return Files.createTempDirectory("gradle-build-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
