package com.github.cuzfrog.gradle.lean;

import org.gradle.api.distribution.plugins.DistributionPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

class InstallDistLean extends AbstractLeanTask {
    private static final Logger logger = LoggerFactory.getLogger(InstallDistLean.class);

    static final String TASK_NAME = "installDistLean";

    private final Jar jarTask;
    private final Sync copyTask;

    public InstallDistLean() {
        final TaskContainer tasks = getProject().getTasks();
        jarTask = (Jar) tasks.getAt(JavaPlugin.JAR_TASK_NAME);
        copyTask = (Sync) tasks.getAt(DistributionPlugin.TASK_INSTALL_NAME);
        this.dependsOn(copyTask);
    }

    @TaskAction
    void taskAction() {
        logger.debug("Try to minimize installed jars.");
        final Path archivePath = jarTask.getArchivePath().toPath();
        final Path installDir = copyTask.getDestinationDir().toPath();
        Minimizer.newInstance(this).minimize(archivePath, installDir.resolve("lib"));
        logger.debug("Installed jars minimized.");
    }
}
