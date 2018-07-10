package com.github.cuzfrog.gradle.lean;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;

abstract class AbstractLeanTask extends DefaultTask {
    private final Property<String[]> classesExcluded;

    AbstractLeanTask() {
        this.classesExcluded = getProject().getObjects().property(String[].class);
    }

    final Property<String[]> getClassesExcluded() {
        return classesExcluded;
    }
}
