package com.evolve.alpaca.gui.export;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PersonExportType {
    ALL("Wszystko"),
    ONLY_VISIBLE("Tylko widoczne");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

}
