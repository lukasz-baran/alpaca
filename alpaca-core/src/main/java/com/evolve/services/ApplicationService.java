package com.evolve.services;

import com.evolve.alpaca.ddd.Command;
import com.evolve.alpaca.ddd.CommandCollector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ApplicationService {

    protected final CommandCollector commandCollector;

    public <COMMAND extends Command> void persistCommand(COMMAND command) {


        commandCollector.addCommand(command);


    }

}
