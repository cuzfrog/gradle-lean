package com.github.cuzfrog.gradle.lean;

import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;

class LeanConfigExtension {
    private final ListProperty<String> excluded;

    public LeanConfigExtension(final Project project){
        this.excluded = project.getObjects().listProperty(String.class);
    }

    ListProperty<String> getExcluded() {
        return excluded;
    }
}
