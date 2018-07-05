package com.github.cuzfrog.gradle.lean;

import org.apache.commons.io.FilenameUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableMap;
import org.gradle.jvm.tasks.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.function.Consumer;

class DistZipLean extends DefaultTask {
    private static final Logger logger = LoggerFactory.getLogger(DistZipLean.class);

    static final String TASK_NAME = "distZipLean";

    private final Path archivePath;
    private final Path zipArchivePath;

    public DistZipLean() {
        final TaskContainer tasks = getProject().getTasks();
        final Jar jarTask = (Jar) tasks.getAt(JavaPlugin.JAR_TASK_NAME);
        archivePath = jarTask.getArchivePath().toPath();
        final Zip zipTask = (Zip) tasks.getAt(ApplicationPlugin.TASK_DIST_ZIP_NAME);
        zipArchivePath = zipTask.getArchivePath().toPath();
        this.dependsOn(zipTask);
    }

    @TaskAction
    void taskAction() throws IOException {
        logger.debug("Try to lean archive '{}'", zipArchivePath);
        final Path minZipPath = genMinZipPath(zipArchivePath);
        try {
            Files.copy(zipArchivePath, minZipPath, StandardCopyOption.REPLACE_EXISTING);
            ZipFsUtils.onZipFileSystem(minZipPath, zipFs -> JarUtils.minimizeJars(archivePath, zipFs.getPath("lib")));
        }finally {
            Files.deleteIfExists(minZipPath);
        }
        logger.debug("Archive '{}' leaning completed.", zipArchivePath);
    }

    private static Path genMinZipPath(final Path zipArchivePath) {
        final String name = FilenameUtils.getBaseName(zipArchivePath.toString()) + "-min.zip";
        return zipArchivePath.resolveSibling(name);
    }
}
