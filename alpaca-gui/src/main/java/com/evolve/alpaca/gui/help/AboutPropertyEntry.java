package com.evolve.alpaca.gui.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AboutPropertyEntry {
    private String propertyName;
    private String propertyValue;

    public static AboutPropertyEntry of(String name, String value) {
        return new AboutPropertyEntry(name, value);
    }
}
