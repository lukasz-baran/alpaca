package com.evolve.alpaca.ddd;

import com.evolve.alpaca.utils.LogUtil;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandCollectorShould {
    public static final String TMPDIR = System.getProperty("java.io.tmpdir");

    final LocalDateTime instant = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
    final Clock clock = Clock.fixed(instant.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);


    @Test
    void serializeContent() {
        // given
        final CommandCollector commandCollector = new CommandCollector(TMPDIR + "/alpaca/commands/", clock);
        final FakeCommand command = new FakeCommand("PERSONID", List.of("FOO", "BAR"));

        // when -- persist command
        PersistedCommand persistedCommand = commandCollector.addCommand(command);

        // then -- assert that the serialized command is the same as the original command
        assertThat(LogUtil.printJson(command))
                .isEqualTo(LogUtil.printJson(persistedCommand.command()));


        // when
        //PersistedCommand deserialized = commandCollector.addCommand(persistedCommand.command());
    }

}