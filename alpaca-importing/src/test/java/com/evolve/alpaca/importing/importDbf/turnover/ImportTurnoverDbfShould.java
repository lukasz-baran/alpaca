package com.evolve.alpaca.importing.importDbf.turnover;

import com.evolve.alpaca.turnover.Turnover;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class ImportTurnoverDbfShould {

    @Test
    @Disabled
    void importTurnovers() throws IOException {
        // given
        var turnoversFile = new DefaultResourceLoader().getResource("OBROTY.DBF");
        assumeThat(turnoversFile.exists())
                .isTrue();

        // when
        final List<Turnover> turnovers = doImport(turnoversFile.getURL());

        // then
        assertThat(turnovers)
                .isNotEmpty();
        //turnovers.forEach(System.out::println);
    }

    static List<Turnover> doImport(URL turnoversFile) {
        return new ImportTurnoverDbf().performImport(turnoversFile)
                .stream()
                .map(DbfTurnover::of)
                .collect(Collectors.toList());
    }
}