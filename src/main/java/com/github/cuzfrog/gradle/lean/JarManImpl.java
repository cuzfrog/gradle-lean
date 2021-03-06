package com.github.cuzfrog.gradle.lean;

import org.vafer.jdependency.Clazz;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Immutable
final class JarManImpl implements JarMan{

    @Override
    public void removeEntry(final Path jarFile, final Set<Clazz> removable) {
        FsUtils.onZipFileSystem(jarFile, rootPath -> {
            try {
                for (final Clazz clazz : removable) {
                    final String classPath = clazz.getName().replaceAll("\\.", "/") + ".class";
                    final Path pathInZipfile = rootPath.resolve(classPath);
                    Files.deleteIfExists(pathInZipfile);
                }
                recursivelyRemoveEmptyDir(rootPath);
            } catch (final IOException e) {
                throw new RuntimeException("Error happened during removing entry in jar:" + jarFile, e);
            }
        });
    }

    /**
     * @return true if the dir is deleted, because it has no children or its children are all deleted
     */
    private static boolean recursivelyRemoveEmptyDir(final Path dir) throws IOException {
        final List<Path> children = Files.list(dir).collect(Collectors.toList());
        boolean noChildren = true;
        for (final Path child : children) {
            if (Files.isDirectory(child)) {
                noChildren = recursivelyRemoveEmptyDir(child) && noChildren;
            } else {
                noChildren = false;
            }
        }
        if (noChildren) {
            Files.deleteIfExists(dir);
        }
        return noChildren;
    }
}
