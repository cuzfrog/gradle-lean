package com.github.cuzfrog.gradle.lean;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.tasks.AbstractCopyTask;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.bundling.Zip;

final class LeanPlugin implements Plugin<Project> {

    private static final String TASK_INSTALL_DIST_NAME = "installDist";

    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply(ApplicationPlugin.class);
        configureTask(project.getTasks().getAt(TASK_INSTALL_DIST_NAME));
    }

    private static void configureTask(final Task task) {
        if (task instanceof AbstractCopyTask) {
            final AbstractCopyTask copyTask = AbstractCopyTask.class.cast(task);
            copyTask.eachFile(LeanPlugin::processFileCopyDetails);
        }
    }

    private static void processFileCopyDetails(final FileCopyDetails fileCopyDetails) {
        System.out.println(fileCopyDetails.getPath());
    }
}
