package com.github.cuzfrog.gradle.lean;

import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vafer.jdependency.Clazz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class JarManTest {
    private Path tmpDir = TestFileSystem.createDir("/tmp");
    private Path jarPath;
    @BeforeEach
    void setup() throws IOException {
        final Path originalJar = Paths.get(
                Resources.getResource("test/animal-sniffer-annotations-1.14.jar").getPath());
        jarPath = Files.copy(originalJar, tmpDir.resolve(originalJar.getFileName().toString()));
    }

    @AfterEach
    void teardown() throws IOException {
        Files.deleteIfExists(jarPath);
    }

    @Test
    void removeEntry() throws IOException {
        final long originalSize = Files.size(jarPath);
        Clazz clazz = new Clazz("org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement");
        JarMan.removeEntry(jarPath, Sets.newHashSet(clazz));
        assertThat(Files.size(jarPath)).isLessThan(originalSize);
    }
}
