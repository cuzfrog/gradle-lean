package com.github.cuzfrog.gradle.lean;

import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ZipFsUtilsTest {

    private static final Path tmpDir = TestFileSystem.createDir("/tmp");
    private final Path zipPath = tmpDir.resolve("test.zip");

    @Test
    void onZipFileSystem() {
        final byte[] content = RandomStringUtils.randomAlphanumeric(200).getBytes();

        ZipFsUtils.onZipFileSystem(zipPath, zipFs -> {
            try {
                Files.write(zipFs.getPath("file1"), content);
            } catch (IOException e) {
                fail("Create new file in new zip failed", e);
            }
        }, true);

        ZipFsUtils.onZipFileSystem(zipPath, zipFs -> {
            try {
                assertThat(Files.readAllBytes(zipFs.getPath("file1"))).isEqualTo(content);
            } catch (IOException e) {
                fail("Read file in zip failed", e);
            }
        });
    }
}
