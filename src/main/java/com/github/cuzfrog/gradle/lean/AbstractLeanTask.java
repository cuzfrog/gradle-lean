package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;

import java.util.List;

abstract class AbstractLeanTask extends DefaultTask {
    private final ListProperty<String> excludedClasses;
    private final ListProperty<String> excludedDependencies;

    AbstractLeanTask() {
        this.excludedClasses = getProject().getObjects().listProperty(String.class);
        this.excludedDependencies = getProject().getObjects().listProperty(String.class);
    }

    final List<String> getExcludedClasses() {
        return excludedClasses.get();
    }
    final List<String> getExcludedDependencies() {
        return excludedDependencies.get();
    }

    final void setProvider(final Provider<List<String>> classesProvider,
                           final Provider<List<String>> dependencyProvider) {
        excludedClasses.set(classesProvider);
        excludedDependencies.set(dependencyProvider);
    }
}
