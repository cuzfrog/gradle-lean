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

        project.getTasks().create(InstallDistLean.TASK_NAME, InstallDistLean.class);
        project.getTasks().create(DistZipLean.TASK_NAME, DistZipLean.class);
    }
}
