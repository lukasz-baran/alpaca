package com.evolve;

import com.evolve.alpaca.ddd.Command;

public record ArchivePersonCommand(String personId) implements Command {
}
