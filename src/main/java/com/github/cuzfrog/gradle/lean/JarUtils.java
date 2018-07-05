package com.github.cuzfrog.gradle.lean;

import org.vafer.jdependency.Clazz;
import org.vafer.jdependency.Clazzpath;
import org.vafer.jdependency.ClazzpathUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class JarUtils {

    static void minimizeJars(final Path archivePath, final Path libDir) throws Exception {
        final Clazzpath cp = new Clazzpath();
        final ClazzpathUnit artifact = cp.addClazzpathUnit(archivePath.toFile());

        final List<Path> dependencyJars = getLibDependencyJars(libDir, archivePath.getFileName().toString());
        for (final Path jar : dependencyJars) {
            cp.addClazzpathUnit(jar.toFile(), jar.getFileName().toString());
        }

        final Set<Clazz> removable = cp.getClazzes();
        removable.removeAll(artifact.getClazzes());
        removable.removeAll(artifact.getTransitiveDependencies());

        for (final Path jar : dependencyJars) {
            JarMan.removeEntry(jar, removable);
        }
    }

    private static List<Path> getLibDependencyJars(final Path libDir, final String artifactName) throws IOException {
        return Files.list(libDir)
                .filter(p -> {
                    final String filename = p.getFileName().toString();
                    return filename.endsWith(".jar") && !filename.equals(artifactName);
                })
                .collect(Collectors.toList());
    }
}
