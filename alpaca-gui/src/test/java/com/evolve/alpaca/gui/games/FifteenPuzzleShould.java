package com.evolve.alpaca.gui.games;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FifteenPuzzleShould {

    @Test
    void returnProperCoordinates() {
        final String id = "imagePane23";

        assertThat(FifteenPuzzle.getRow(id))
                .isEqualTo(2);

        assertThat(FifteenPuzzle.getColumn(id))
                .isEqualTo(3);
    }

}