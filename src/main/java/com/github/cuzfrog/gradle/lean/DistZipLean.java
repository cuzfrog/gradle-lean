package com.github.cuzfrog.gradle.lean;

import org.apache.commons.io.FilenameUtils;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.jvm.tasks.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

class DistZipLean extends AbstractLeanTask {
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
    void taskAction() throws Exception {
        logger.debug("Try to lean archive '{}'", zipArchivePath);
        final Path minZipPath = genMinZipPath(zipArchivePath);
        try {
            Files.copy(zipArchivePath, minZipPath, StandardCopyOption.REPLACE_EXISTING);

            FsUtils.onZipFileSystem(minZipPath, rootPath -> { //todo: try to remove hardcoded 'lib'
                final Path libDir = rootPath.resolve(FilenameUtils.getBaseName(zipArchivePath.toString())).resolve("lib");
                Minimizer.newInstance().minimize(archivePath, libDir);
            });
            logger.debug("Archive '{}' leaning completed.", zipArchivePath);
        }catch (final Exception e){
            logger.error("Archive '{}' leaning failed:", zipArchivePath, e);
            Files.deleteIfExists(minZipPath);
            throw e;
        }
    }

    private static Path genMinZipPath(final Path zipArchivePath) {
        final String name = FilenameUtils.getBaseName(zipArchivePath.toString()) + "-min.zip";
        return zipArchivePath.resolveSibling(name);
    }
}
