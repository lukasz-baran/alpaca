package com.evolve.alpaca.ddd;

import com.evolve.alpaca.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommandsApplier {
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;
    private final Path pathToCommandsDir;

    public CommandsApplier(@Value("${alpaca.commands.store}") String pathToCommands) {
        this.pathToCommandsDir = Path.of(pathToCommands);
    }


    public List<PersistedCommand> loadCommands() {
        final List<PersistedCommand> commands = new ArrayList<>();
        try {
            Files.list(pathToCommandsDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> loadCommand(commands, path));
        } catch (IOException e) {
            log.error("Cannot load commands - " + e);
        }
        return commands;
    }

    private void loadCommand(final List<PersistedCommand> commands, Path path) {
        try {
            final String content = Files.readString(path.toFile().toPath());
            final PersistedCommand persistedCommand = objectMapper.readValue(content, PersistedCommand.class);
            commands.add(persistedCommand);
        } catch (IOException e) {
            log.error("Cannot read file - " + e);
        }
    }
}
