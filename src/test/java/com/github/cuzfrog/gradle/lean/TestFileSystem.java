package com.github.cuzfrog.gradle.lean;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestFileSystem {

    private static final FileSystem memFs = Jimfs.newFileSystem(Configuration.unix());

    private TestFileSystem() {}

    static Path createConcreteTempDir() {
        try {
            return Files.createTempDirectory("gradle-build-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path createDir(final String path) {
        final Path p = memFs.getPath(path);
        if (!Files.exists(p)) {
            try {
                Files.createDirectory(p);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return p;
    }
}
