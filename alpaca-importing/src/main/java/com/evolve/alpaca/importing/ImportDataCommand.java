package com.evolve.alpaca.importing;

public record ImportDataCommand(String personsFilePath, String accountsFilePath, String docFilePath) {
}
