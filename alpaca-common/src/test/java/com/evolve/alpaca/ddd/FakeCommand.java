package com.evolve.alpaca.ddd;

import java.util.List;

public record FakeCommand(String personId, List<String> relations) implements Command {
}
