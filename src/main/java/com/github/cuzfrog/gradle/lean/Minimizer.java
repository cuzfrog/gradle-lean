package com.github.cuzfrog.gradle.lean;

import java.nio.file.Path;

interface Minimizer {
    /**
     * Analyze and minimize dependencies.
     *
     * @param archivePath the application artifact jar path
     * @param libDir      the directory where dependency jars reside
     */
    void minimize(final Path archivePath, final Path libDir);

    static Minimizer newInstance(final AbstractLeanTask leanTask) {
        return new MinimizerImpl(JarMan.newInstance(), leanTask.getExcludedClasses(), leanTask.getExcludedDependencies());
    }
}
