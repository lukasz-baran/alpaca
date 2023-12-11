package com.evolve.alpaca.ddd;

import java.time.LocalDateTime;

public record PersistedCommand(String clazz, LocalDateTime when, Object command) {
}
