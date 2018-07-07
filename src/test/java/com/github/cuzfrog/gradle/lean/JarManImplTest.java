package com.github.cuzfrog.gradle.lean;

import com.google.common.collect.Sets;
import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.vafer.jdependency.Clazz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

final class JarManImplTest {
    private static final Path tmpDir = TestFileSystem.createDir("/tmp");
    private final Path jarPath = genTestJar();

    private final JarMan jarMan = JarMan.newInstance();

    @AfterEach
    void teardown() throws IOException {
        Files.deleteIfExists(jarPath);
    }

    @Test
    void reduceJarSize() throws IOException {
        final long originalSize = Files.size(jarPath);
        final Clazz remove1 = new Clazz("com.github.cuzfrog.gradle.lean.LeanPlugin");
        final Clazz noisy = new Clazz("some.other.package.SomeClass");

        jarMan.removeEntry(jarPath, Sets.newHashSet(remove1, noisy));
        assertThat(Files.size(jarPath)).isLessThan(originalSize);

        ZipFsUtils.onZipFileSystem(jarPath, rootPath -> {
            assertThat(rootPath.resolve("empty")).doesNotExist();
            assertThat(rootPath.resolve("com/github/cuzfrog/gradle")).doesNotExist();
            assertThat(rootPath.resolve("com/github/cuzfrog/sbt")).exists();
        });
    }

    /**
     * Package layout: <br><br>
     * <p>
     * com.github.cuzfrog<br>
     * +-- gradle.leanLeanPlugin<br>
     * +-- sbt.TmpfsPlugin<br>
     * empty.sub
     */
    private static Path genTestJar() {
        final Path jarPath = tmpDir.resolve("my-test.jar");
        ZipFsUtils.onZipFileSystem(jarPath, rootPath -> {
            try {
                final Path webInf = rootPath.resolve("META-INF");
                Files.createDirectory(webInf);
                Files.write(webInf.resolve("MANIFEST.MF"), randomBytes());

                final Path d1 = Files.createDirectory(rootPath.resolve("com"));
                final Path d2 = Files.createDirectory(d1.resolve("github"));
                final Path d3 = Files.createDirectory(d2.resolve("cuzfrog"));

                final Path d41 = Files.createDirectory(d3.resolve("gradle"));
                final Path d51 = Files.createDirectory(d41.resolve("lean"));
                Files.write(d51.resolve("LeanPlugin.class"), randomBytes());

                final Path d42 = Files.createDirectory(d3.resolve("sbt"));
                Files.write(d42.resolve("TmpfsPlugin.class"), randomBytes());

                final Path empty = Files.createDirectory(rootPath.resolve("empty"));
                Files.createDirectory(empty.resolve("sub"));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }, true);
        return jarPath;
    }

    private static byte[] randomBytes() {
        return RandomStringUtils.randomAscii(200).getBytes();
    }
}
