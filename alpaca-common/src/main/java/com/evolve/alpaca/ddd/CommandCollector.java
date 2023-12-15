package com.evolve.alpaca.ddd;

import com.evolve.alpaca.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class CommandCollector {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;

    private final Path pathToCommandsDir;
    private final Clock clock;

    /**
     * We can use this flag to stop recording commands
     */
    private final AtomicBoolean isRecording = new AtomicBoolean(true);

    public CommandCollector(@Value("${alpaca.commands.store}") String pathToCommands, Clock clock) {
        this.pathToCommandsDir = Path.of(pathToCommands);
        this.clock = clock;
    }

    @SneakyThrows
    public PersistedCommand addCommand(Command command) {
        if (!isRecording.get()) {
            return null;
        }

        final String type = command.getClass().getName();
        final LocalDateTime now = LocalDateTime.now(clock);
        final PersistedCommand persistedCommand = new PersistedCommand(type, now, command);

        final String fileName = now.format(DATE_TIME_FORMATTER) + "-" + command.getClass().getSimpleName() +".json";

        final String json = objectMapper.writeValueAsString(persistedCommand);
        saveCommandToDisk(json, fileName);

        return objectMapper.readValue(json, PersistedCommand.class);
    }

    private void saveCommandToDisk(String content, String fileName) {
        Path path;
        try {
            path = Files.createDirectories(pathToCommandsDir);
        } catch (IOException e) {
            log.error("Cannot create directories - " + e);
            return;
        }

        try {
            Path createdFile = Files.writeString(path.resolve(fileName), content);
            log.info("File created: " + createdFile);
        } catch (IOException e) {
            log.error("Cannot write to file - " + e);
        }
    }

    public void stopRecording() {
        isRecording.set(false);
    }

    public void startRecording() {
        isRecording.set(true);
    }
}
