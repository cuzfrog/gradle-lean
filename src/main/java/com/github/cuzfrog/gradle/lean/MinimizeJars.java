package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting;
import org.gradle.jvm.tasks.Jar;
import org.vafer.jdependency.Clazz;
import org.vafer.jdependency.Clazzpath;
import org.vafer.jdependency.ClazzpathUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class MinimizeJars extends DefaultTask {

    private final File archivePath;
    private final File installDir;
    private final Clazzpath cp = new Clazzpath();

    public MinimizeJars() {
        final Jar jarTask = (Jar) getProject().getTasks().getAt(JavaPlugin.JAR_TASK_NAME);
        archivePath = jarTask.getArchivePath();
        final Sync copyTask = (Sync) getProject().getTasks().getAt(DistributionPlugin.TASK_INSTALL_NAME);
        installDir = copyTask.getDestinationDir();
    }

    @VisibleForTesting
    MinimizeJars(final File archivePath, final File installDir){
        this.archivePath = archivePath;
        this.installDir = installDir;
    }

    @TaskAction
    void minimizeJars() throws Exception {
        final String artifactName = archivePath.getName();
        final ClazzpathUnit artifact = cp.addClazzpathUnit(archivePath, artifactName);
        for (final File jar : getLibJars(installDir.toPath(), artifactName)) {
            cp.addClazzpathUnit(jar, jar.getName());
        }

        final Set<Clazz> removable = cp.getClazzes();
        removable.removeAll(artifact.getClazzes());
        removable.removeAll(artifact.getTransitiveDependencies());

        removable.forEach(System.out::println);
    }

    private static List<File> getLibJars(final Path installDir, final String artifactName) throws IOException {
        return Files.list(installDir.resolve("lib"))
                .filter(p -> {
                    final String name = p.getFileName().toString();
                    return name.endsWith(".jar") && !name.equals(artifactName);
                }).map(Path::toFile).collect(Collectors.toList());
    }
}
