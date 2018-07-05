package com.github.cuzfrog.gradle.lean;

import org.vafer.jdependency.Clazz;

import java.nio.file.Path;
import java.util.Set;

interface JarMan {
    void removeEntry(final Path jarFile, final Set<Clazz> removable);

    static JarMan newInstance() {
        return new JarManImpl();
    }
}
