package com.github.cuzfrog.gradle.lean;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LeanPlugin implements Plugin<Project> {
    private static final Logger logger = LoggerFactory.getLogger(LeanPlugin.class);

    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply(ApplicationPlugin.class);
        project.getPluginManager().apply(JavaPlugin.class);

        project.getTasks().create("minimizeJars", MinimizeJars.class);

        System.out.println("------Plugin configuration applied.------");
    }
}
