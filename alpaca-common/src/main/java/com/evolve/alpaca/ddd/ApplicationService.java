package com.evolve.alpaca.ddd;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ApplicationService {

    protected final CommandCollector commandCollector;

    public <COMMAND extends Command> void persistCommand(COMMAND command) {
        commandCollector.addCommand(command);
    }

}
