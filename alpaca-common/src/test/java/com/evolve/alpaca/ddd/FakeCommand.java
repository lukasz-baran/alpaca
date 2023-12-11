package com.evolve.alpaca.ddd;

import java.util.List;

record FakeCommand(String personId, List<String> relations) implements Command {
}
