package com.evolve.alpaca.gui.games;

import org.apache.commons.lang.StringUtils;

public class FifteenPuzzle {

    public static final String PREFIX_BTN_NAME = "imagePane";

    static int getRow(String id) {
        var ch = StringUtils.removeStart(id, PREFIX_BTN_NAME).substring(0, 1);
        return Integer.parseInt(ch);
    }

    static int getColumn(String id) {
        var ch = StringUtils.removeStart(id, PREFIX_BTN_NAME).substring(1, 2);
        return Integer.parseInt(ch);
    }
}
