package com.github.cuzfrog.gradle.lean;

import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class FsUtilsTest {

    private static final Path tmpDir = TestFileSystem.createDir("/tmp");
    private final Path zipPath = tmpDir.resolve("test.zip");

    @Test
    void onZipFileSystem() {
        final byte[] content = RandomStringUtils.randomAlphanumeric(200).getBytes();

        FsUtils.onZipFileSystem(zipPath, rootPath -> {
            try {
                Files.write(rootPath.resolve("file1"), content);
            } catch (IOException e) {
                fail("Create new file in new zip failed", e);
            }
        }, true);

        FsUtils.onZipFileSystem(zipPath, rootPath -> {
            try {
                assertThat(Files.readAllBytes(rootPath.resolve("file1"))).isEqualTo(content);
            } catch (IOException e) {
                fail("Read file in zip failed", e);
            }
        });
    }
}
