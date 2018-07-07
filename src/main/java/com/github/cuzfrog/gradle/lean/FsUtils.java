package com.github.cuzfrog.gradle.lean;

import com.sun.nio.zipfs.ZipFileSystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

final class FsUtils {
    private static final Map<String, String> noNewZipProps = Collections.singletonMap("create", "false");
    private static final Map<String, String> newZipProps = Collections.singletonMap("create", "true");
    private static final Path tmpDir = createTmpDir();

    static void onZipFileSystem(final Path archivePath, final Consumer<Path> action) {
        onZipFileSystem(archivePath, action, false);
    }

    static void onZipFileSystem(final Path archivePath, final Consumer<Path> action, final boolean createNew) {
        try {
            if (archivePath.getFileSystem() instanceof ZipFileSystem) {
                final Path tmpAccessPath = tmpDir.resolve(archivePath.getFileName().toString());
                Files.copy(archivePath, tmpAccessPath, StandardCopyOption.REPLACE_EXISTING);
                openZipFileSystemOnArchive(tmpAccessPath, action, createNew);
                Files.move(tmpAccessPath, archivePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                openZipFileSystemOnArchive(archivePath, action, createNew);
            }
        } catch (final IOException e) {
            throw new RuntimeException(String.format("Failed to act on archive '%s'", archivePath), e);
        }
    }

    private static void openZipFileSystemOnArchive(final Path archivePath,
                                                   final Consumer<Path> action,
                                                   final boolean createNew) throws IOException {
        final URI jarUri = URI.create("jar:" + archivePath.toUri());
        final Map<String, String> zipProps = createNew ? newZipProps : noNewZipProps;

        try (final FileSystem zipfs = FileSystems.newFileSystem(jarUri, zipProps)) {
            action.accept(zipfs.getPath("/"));
        }
    }

    private static Path createTmpDir() {
        try {
            return Files.createTempDirectory("gradle-lean-tmp-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
