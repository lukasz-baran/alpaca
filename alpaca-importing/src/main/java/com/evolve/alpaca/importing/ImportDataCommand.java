package com.evolve.alpaca.importing;

public record ImportDataCommand(String personsFilePath, String accountsFilePath, String docFilePath,
                                ImportProgressListener listener) {

    public interface ImportProgressListener {
        void step(double progress, String messsage);
    }
}
