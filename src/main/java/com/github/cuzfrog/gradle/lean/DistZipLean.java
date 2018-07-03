package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.jvm.tasks.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

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
    void minimizeJars() throws Exception {
        System.out.println(zipArchivePath);
    }
}
