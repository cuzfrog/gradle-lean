package com.github.cuzfrog.gradle.lean;

import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.vafer.jdependency.Clazz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

final class MinimizerImplTest {

    private static final String TEST_ARTIFACT_JAR_NAME = "my-test.jar";
    private static final String RES_JAR1 = "irrelevant.jar";
    private static final String RES_JAR2 = "jimfs-1.1.jar";

    private final Path tmpDir = TestFileSystem.createConcreteTempDir();

    private final JarMan mockJarMan = mock(JarMan.class);
    private final Set<String> excludedDependencies = Sets.newHashSet(
            "com.github.cuzfrog:excluded:0.0.2", "com.github.cuzfrog:noisy:3.0.2");
    private final MinimizerImpl minimizer = new MinimizerImpl(mockJarMan, new ArrayList<>(), excludedDependencies);
    private final Path archive = genTestArtifactJar(tmpDir.resolve(TEST_ARTIFACT_JAR_NAME));
    private Path libDir;

    private final ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
    @SuppressWarnings("unchecked")
    private final ArgumentCaptor<Set<Clazz>> clazzesCaptor = ArgumentCaptor.forClass(Set.class);

    @BeforeEach
    void setup() throws Exception {
        final Path libSourceDir = Paths.get(Resources.getResource("testJars/lib").toURI());
        libDir = Files.createDirectory(tmpDir.resolve("test-lib"));
        for (final Path sourceJar : (Iterable<Path>) Files.list(libSourceDir)::iterator) {
            final Path targetLibJar = libDir.resolve(sourceJar.getFileName());
            Files.copy(sourceJar, targetLibJar);
        }
    }

    @AfterEach
    void clear() {
        TestFileSystem.deleteDir(tmpDir);
    }

    @Test
    void minimizeJars() {
        minimizer.minimize(archive, libDir);

        verify(mockJarMan, times(2)).removeEntry(pathCaptor.capture(), clazzesCaptor.capture());
        final List<Path> expectedLibJars = new ArrayList<>();
        expectedLibJars.add(libDir.resolve(RES_JAR1));
        expectedLibJars.add(libDir.resolve(RES_JAR2));

        assertThat(pathCaptor.getAllValues()).containsExactlyInAnyOrderElementsOf(expectedLibJars);
        assertThat(clazzesCaptor.getValue()).isNotEmpty();
    }

    @Test
    void getDependencyJars() throws Exception {
        genJar(libDir.resolve("excluded-0.0.2.jar"), Sets.newHashSet("com.github.cuzfrog.test.Excluded"));
        Files.createFile(libDir.resolve(TEST_ARTIFACT_JAR_NAME));

        final List<Path> depsToMinimize = minimizer.getLibDependencyJars(libDir, archive.getFileName().toString());

        final List<Path> expectedLibJars = new ArrayList<>();
        expectedLibJars.add(libDir.resolve(RES_JAR1));
        expectedLibJars.add(libDir.resolve(RES_JAR2));
        assertThat(depsToMinimize).containsExactlyInAnyOrderElementsOf(expectedLibJars);
    }

    private static Path genTestArtifactJar(final Path jarPath) {
        FsUtils.onZipFileSystem(jarPath, rootPath -> {
            try {
                final Path targetDir = rootPath.resolve("com/github/cuzfrog/gradle/lean");
                Files.createDirectories(targetDir);
                final Path source = Paths.get(Resources.getResource("testJars/TestFileSystem.class").toURI());
                Files.copy(source, targetDir.resolve("TestFileSystem.class"));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }, true);
        return jarPath;
    }

    private static void genJar(final Path jarPath, final Set<String> classes) {
        FsUtils.onZipFileSystem(jarPath, rootPath -> classes
                .stream()
                .map(c -> c.replaceAll("\\.", "/"))
                .map(c -> c.toLowerCase().endsWith(".class") ? c : c + ".class")
                .map(rootPath::resolve)
                .forEach(p -> {
                    try {
                        if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());
                        Files.write(p, RandomStringUtils.randomAlphanumeric(200).getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }), true);
    }
}
