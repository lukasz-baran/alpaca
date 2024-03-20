package com.evolve.alpaca.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class FileUtils {

    public static Optional<File> createFileWithDirectories(String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.getParentFile().mkdirs()) {
                    log.warn("Unable to create the configuration directory");
                    return Optional.empty();
                }
                if (file.createNewFile()) {
                    log.info("File was created");
                } else {
                    log.warn("Unable to create the configuration file");
                }
            } catch (IOException e) {
                log.error("Unable to create the configuration file", e);
            }
        }
        return Optional.of(file);
    }
}
