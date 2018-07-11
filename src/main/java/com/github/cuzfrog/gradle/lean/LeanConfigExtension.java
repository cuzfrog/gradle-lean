package com.github.cuzfrog.gradle.lean;

import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;

class LeanConfigExtension {
    private final ListProperty<String> excludedClasses;
    private final ListProperty<String> excludedDependencies;

    public LeanConfigExtension(final Project project){
        this.excludedClasses = project.getObjects().listProperty(String.class);
        this.excludedDependencies = project.getObjects().listProperty(String.class);
    }

    ListProperty<String> getExcludedClasses() {
        return excludedClasses;
    }
    ListProperty<String> getExcludedDependencies() {
        return excludedDependencies;
    }
}
