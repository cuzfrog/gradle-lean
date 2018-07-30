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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

final class LeanPluginTest {

    private Path buildDir;

    @BeforeEach
    void setup() throws Exception {
        buildDir = TestFileSystem.createConcreteTempDir();
        FileUtils.copyDirectory(new File(Resources.getResource("build-root").getPath()), buildDir.toFile());
        System.out.println("Build dir:" + buildDir);
    }

    @AfterEach
    void clear() {
        TestFileSystem.deleteDir(buildDir);
    }

    //todo: read distPath from test build.gradle
    @Test
    void installDistLean() throws Exception {
        final BuildResult result = GradleRunner.create()
                .withProjectDir(buildDir.toFile())
                .forwardOutput()
                .withArguments(InstallDistLean.TASK_NAME, "--stacktrace")
                .withPluginClasspath()
                .build();
        assertThat(result.taskPaths(TaskOutcome.SUCCESS)).contains(":installDist", ":installDistLean");

        final Path aJar = buildDir.resolve("build/install/lean-test-app/lib/guava-23.0.jar");
        assertThat(Files.size(aJar)).isLessThan(200_000);
        assertExcludedClassesExist(aJar);

        final Path jarExcluded = buildDir.resolve("build/install/lean-test-app/lib/jimfs-1.1.jar");
        assertThat(jarExcluded).exists();
        assertThat(Files.size(jarExcluded)).isGreaterThan(200_000);

        final Path jarMinimized = buildDir.resolve("build/install/lean-test-app/lib/junit-4.12.jar");
        assertThat(jarMinimized).exists();
        assertThat(Files.size(jarMinimized)).isLessThan(6_000);
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

        final Path zip = buildDir.resolve("build/distributions/lean-test-app-0.0.1.zip");
        final Path minZip = buildDir.resolve("build/distributions/lean-test-app-0.0.1-min.zip");
        assertThat(Files.size(minZip)).isLessThan(Files.size(zip));
        assertThat(Files.size(minZip)).isLessThan(400_000);

        final Path aJar = buildDir.resolve("guava-23.0.jar");
        final Path jarExcluded = buildDir.resolve("jimfs-1.1.jar");
        final Path jarMinimized = buildDir.resolve("junit-4.12.jar");
        FsUtils.onZipFileSystem(minZip, rootPath -> {
            try {
                Files.copy(rootPath.resolve("lean-test-app-0.0.1/lib/guava-23.0.jar"), aJar);
                Files.copy(rootPath.resolve("lean-test-app-0.0.1/lib/jimfs-1.1.jar"), jarExcluded);
                Files.copy(rootPath.resolve("lean-test-app-0.0.1/lib/junit-4.12.jar"), jarMinimized);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertExcludedClassesExist(aJar);
        assertThat(Files.size(jarExcluded)).isGreaterThan(200_000);
        assertThat(Files.size(jarMinimized)).isLessThan(6_000);
    }

    private static void assertExcludedClassesExist(final Path guavaJar) {
        FsUtils.onZipFileSystem(guavaJar, rootPath -> {
            try {
                final Path cachePkg = rootPath.resolve("com/google/common/cache");
                assertThat(cachePkg).exists();
                assertThat(Files.list(cachePkg).count()).isGreaterThan(21);

                final Path ioPkg = rootPath.resolve("com/google/common/io/ByteSink.class");
                assertThat(ioPkg).exists();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}