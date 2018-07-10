package com.github.cuzfrog.gradle.lean;

final class LeanConfigExtension {
    private String[] excluded;
    public String[] getExcluded() {
        return excluded;
    }
    public void setExcluded(final String[] classesExcluded) {
        this.excluded = classesExcluded;
    }
}
