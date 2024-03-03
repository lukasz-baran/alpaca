package com.evolve.alpaca.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentCategory {

    DEFAULT("(brak)"),
    FORM("Formularze"),
    CORRESPONDENCE("Korespondencja"),
    OTHER("Inne");

    private final String category;

    @Override
    public String toString() {
        return this.category;
    }

}
