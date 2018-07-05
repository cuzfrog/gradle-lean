package com.github.cuzfrog.gradle.lean;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class JarUtilsTest {

    private final JarMan mockJarMan = mock(JarMan.class);
    private final JarUtils jarUtils = new JarUtils(mockJarMan);

    @Test
    void internalMinimizeJars() {
        
    }
}
