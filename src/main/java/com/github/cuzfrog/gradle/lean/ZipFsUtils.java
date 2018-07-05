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
    private static final Map<String, String> noNewZipProps = ImmutableMap.of("create", "false");
    private static final Map<String, String> newZipProps = ImmutableMap.of("create", "true");

    static void onZipFileSystem(final Path archivePath, final Consumer<FileSystem> action) throws IOException {
        onZipFileSystem(archivePath, action, false);
    }

    static void onZipFileSystem(final Path archivePath, final Consumer<FileSystem> action, final boolean createNew) throws IOException {
        final URI jarUri = URI.create("jar:" + archivePath.toUri());
        final Map<String, String> zipProps = createNew ? newZipProps : noNewZipProps;
        try (final FileSystem zipfs = FileSystems.newFileSystem(jarUri, zipProps)) {
            action.accept(zipfs);
        }
    }
}
