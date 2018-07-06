package com.github.cuzfrog.gradle.lean;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

final class LeanPluginTest {

    private Path buildDir;

    @BeforeEach
    void setup() throws Exception {
        buildDir = TestFileSystem.createConcreteTempDir();
        FileUtils.copyDirectory(new File(Resources.getResource("build-root").getPath()), buildDir.toFile());
        System.out.println("Build dir:" + buildDir);
    }

    @AfterEach
    void clear(){
        TestFileSystem.deleteDir(buildDir);
    }

    @Test
    void installDistLean() throws Exception {
        final BuildResult result = GradleRunner.create()
                .withProjectDir(buildDir.toFile())
                .forwardOutput()
                .withArguments(InstallDistLean.TASK_NAME, "--stacktrace")
                .withPluginClasspath()
                .build();
        assertThat(result.taskPaths(TaskOutcome.SUCCESS)).contains(":installDist", ":installDistLean");

        final Path aJar = buildDir.resolve("build/install/gradle-lean-test/lib/guava-23.0.jar");
        assertThat(Files.size(aJar)).isLessThan(20000);
    }

    @Test
    void distZipLean() throws Exception {
        final BuildResult result = GradleRunner.create()
                .withProjectDir(buildDir.toFile())
                .forwardOutput()
                .withArguments(DistZipLean.TASK_NAME, "--stacktrace")
                .withPluginClasspath()
                .build();
        assertThat(result.taskPaths(TaskOutcome.SUCCESS)).contains(":distZip", ":distZipLean");

        Files.list(buildDir).forEach(System.out::println);
    }
}