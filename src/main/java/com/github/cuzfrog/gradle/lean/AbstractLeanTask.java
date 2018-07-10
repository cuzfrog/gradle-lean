package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;

import java.util.List;

abstract class AbstractLeanTask extends DefaultTask {
    private final ListProperty<String> classesExcluded;

    AbstractLeanTask() {
        this.classesExcluded = getProject().getObjects().listProperty(String.class);
    }

    final List<String> getClassesExcluded() {
        return classesExcluded.get();
    }

    final void setProvider(final Provider<List<String>> provider){
        classesExcluded.set(provider);
    }
}
