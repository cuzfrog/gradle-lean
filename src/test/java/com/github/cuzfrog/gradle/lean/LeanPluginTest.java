package com.github.cuzfrog.gradle.lean;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

final class LeanPluginTest {

    private Path buildDir;

    @BeforeEach
    void setup() throws Exception {
        buildDir = TestFileSystem.createConcreteTempDir();
        FileUtils.copyDirectory(new File(Resources.getResource("build-root").getPath()), buildDir.toFile());
        System.out.println("Build dir:" + buildDir);
    }

    @Test
    void apply() throws Exception {
        final BuildResult result = GradleRunner.create()
                .withProjectDir(buildDir.toFile())
                .forwardOutput()
                .withArguments("installDist", "minimizeJars", "--stacktrace")
                .withPluginClasspath()
                .build();
        Files.list(buildDir).forEach(System.out::println);
    }
}