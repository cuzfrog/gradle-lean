package com.github.cuzfrog.gradle.lean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdependency.Clazz;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class JarMan {
    private static final Map<String, String> properties = new HashMap<String, String>() {{
        put("create", "false");
    }};

    static void removeEntry(final Path jarFile, final Set<Clazz> removable) throws IOException {
        final URI jarUri = URI.create("jar:" + jarFile.toUri());
        try (final FileSystem zipfs = FileSystems.newFileSystem(jarUri, properties)) {
            for (final Clazz clazz : removable) {
                final String classPath = clazz.getName().replaceAll("\\.", "/") + ".class";
                System.out.println("Try to remove class:" + classPath);
                final Path pathInZipfile = zipfs.getPath(classPath);
                Files.deleteIfExists(pathInZipfile);
            }

            recursivelyRemoveEmptyDir(zipfs.getPath("/"));
        }
    }

    private static void recursivelyRemoveEmptyDir(final Path dir) throws IOException {
        final List<Path> children = Files.list(dir).collect(Collectors.toList());
        if (children.isEmpty()) {
            Files.deleteIfExists(dir);
        } else {
            for (final Path child : children) {
                if (Files.isDirectory(child)) {
                    recursivelyRemoveEmptyDir(child);
                }
            }
        }
    }
}
