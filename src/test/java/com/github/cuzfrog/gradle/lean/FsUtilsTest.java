package com.github.cuzfrog.gradle.lean;

import org.gradle.internal.impldep.aQute.bnd.build.Run;
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.gradle.internal.impldep.org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.gradle.internal.impldep.org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

final class FsUtilsTest {

    private static final Path tmpDir = TestFileSystem.createDir("/tmp");
    private final Path zipPath = tmpDir.resolve("test.zip");
    private final Path tarPath = tmpDir.resolve("test.tar");
    private final byte[] content = RandomStringUtils.randomAlphanumeric(200).getBytes();

    @AfterEach
    void clear() throws IOException {
        Files.deleteIfExists(zipPath);
        Files.deleteIfExists(tarPath);
    }

    @Test
    void onZipFileSystem() {
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

    @Test
    void onTarFileSystem() throws IOException {
        try(final TarArchiveOutputStream tarOutput = new TarArchiveOutputStream(Files.newOutputStream(tarPath))){
            final TarArchiveEntry entry = new TarArchiveEntry("file1");
            entry.setSize(content.length);
            tarOutput.putArchiveEntry(entry);
            tarOutput.write(content);
            tarOutput.closeArchiveEntry();
        }

        FsUtils.onTarFileSystem(tarPath, rootPath -> {
            try {
                Files.list(rootPath).forEach(System.out::println);
                assertThat(rootPath.resolve(tarPath)).exists().hasBinaryContent(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
