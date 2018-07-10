package com.github.cuzfrog.gradle.lean;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;

final class LeanPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply(ApplicationPlugin.class);
        project.getPluginManager().apply(JavaPlugin.class);

        final LeanConfigExtension extension = project.getExtensions()
                .create("leanConfig", LeanConfigExtension.class, project);

        project.getTasks().create(InstallDistLean.TASK_NAME, InstallDistLean.class, t -> config(t, extension));
        project.getTasks().create(DistZipLean.TASK_NAME, DistZipLean.class, t -> config(t, extension));
    }

    private static void config(final AbstractLeanTask leanTask,
                               final LeanConfigExtension leanConfigExtension) {
        leanTask.getClassesExcluded().set(leanConfigExtension.getExcluded());
    }
}
