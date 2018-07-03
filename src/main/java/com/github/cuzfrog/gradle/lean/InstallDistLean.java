package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting;
import org.gradle.jvm.tasks.Jar;
import org.vafer.jdependency.Clazz;
import org.vafer.jdependency.Clazzpath;
import org.vafer.jdependency.ClazzpathUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class InstallDistLean extends DefaultTask {
    static final String TASK_NAME = "installDistLean";

    private final Path archivePath;
    private final Path installDir;

    public InstallDistLean() {
        final TaskContainer tasks = getProject().getTasks();
        final Jar jarTask = (Jar) tasks.getAt(JavaPlugin.JAR_TASK_NAME);
        archivePath = jarTask.getArchivePath().toPath();
        final Sync copyTask = (Sync) tasks.getAt(DistributionPlugin.TASK_INSTALL_NAME);
        installDir = copyTask.getDestinationDir().toPath();
        this.dependsOn(copyTask);
    }

    @VisibleForTesting
    InstallDistLean(final Path archivePath, final Path installDir) {
        this.archivePath = archivePath;
        this.installDir = installDir;
    }

    @TaskAction
    void minimizeJars() throws Exception {
        final Clazzpath cp = new Clazzpath();
        final ClazzpathUnit artifact = cp.addClazzpathUnit(archivePath.toFile());

        final List<Path> dependencyJars = getLibDependencyJars(installDir, archivePath.getFileName().toString());
        for (final Path jar : dependencyJars) {
            cp.addClazzpathUnit(jar.toFile(), jar.getFileName().toString());
        }

        final Set<Clazz> removable = cp.getClazzes();
        removable.removeAll(artifact.getClazzes());
        removable.removeAll(artifact.getTransitiveDependencies());

        for (final Path jar : dependencyJars) {
            System.out.println("Try to reduce:" + jar);
            JarMan.removeEntry(jar, removable);
        }
    }

    private static List<Path> getLibDependencyJars(final Path dir, final String artifactName) throws IOException {
        return Files.list(dir.resolve("lib"))
                .filter(p -> {
                    final String filename = p.getFileName().toString();
                    return filename.endsWith(".jar") && !filename.equals(artifactName);
                })
                .collect(Collectors.toList());
    }
}
