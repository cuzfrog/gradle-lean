package com.github.cuzfrog.gradle.lean;

import org.gradle.internal.impldep.com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

final class ZipFsUtils {
    private static final Map<String, String> properties = ImmutableMap.of("create", "false");

    static void onZipFileSystem(final Path archivePath, final Consumer<FileSystem> action) throws IOException {
        final URI jarUri = URI.create("jar:" + archivePath.toUri());
        try (final FileSystem zipfs = FileSystems.newFileSystem(jarUri, properties)) {
            action.accept(zipfs);
        }
    }
}
