package com.github.cuzfrog.gradle.lean;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

final class ZipFsUtils {
    private static final Map<String, String> noNewZipProps = genProperties("create", "false");
    private static final Map<String, String> newZipProps = genProperties("create", "true");

    static void onZipFileSystem(final Path archivePath, final Consumer<FileSystem> action) {
        onZipFileSystem(archivePath, action, false);
    }

    static void onZipFileSystem(final Path archivePath, final Consumer<FileSystem> action, final boolean createNew) {
        final URI jarUri = URI.create("jar:" + archivePath.toUri());
        final Map<String, String> zipProps = createNew ? newZipProps : noNewZipProps;
        try (final FileSystem zipfs = FileSystems.newFileSystem(jarUri, zipProps)) {
            action.accept(zipfs);
        }catch (final IOException e){
            throw new RuntimeException("Failed to open zip filesystem, archive path:" + archivePath, e);
        }
    }

    private static Map<String, String> genProperties(final String k, final String v){
        return new HashMap<String, String>(){{
            put(k, v);
        }};
    }
}
