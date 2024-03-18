package com.evolve.alpaca.importing.importDbf;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO This test is only experimental - we wanted to isolate the problem
 */
@RequiredArgsConstructor
public class RegistryNumbersShould {


    @Test
    void readAllNumbers() {
        final Resource resource = new DefaultResourceLoader().getResource("registry-numbers.csv");

        RegistryNumbers.RegistryNumbersLoader registryNumbers = RegistryNumbers.usingLoader(resource);

        registryNumbers.loadNumbers();
    }

    @Test
    void loadDataForSingleRecord() {
        // given
        final RegistryNumbers registryNumbers = new RegistryNumbers();

        // when
        final RegistryNumbers.Numbers halina = registryNumbers.parseLine("   100  1246"); // correct 100

        // then
        assertThat(halina.getNumber()).hasValue(1246);
        assertThat(halina.getOldNumber()).hasValue(100);

        // when
        final RegistryNumbers.Numbers jadwiga = registryNumbers.parseLine("1 069  0100"); // correct 1069

        // then
        assertThat(jadwiga.getNumber()).hasValue(100);
        assertThat(jadwiga.getOldNumber()).hasValue(1069);

        // when
        final RegistryNumbers.Numbers stanislaw = registryNumbers.parseLine("953 1189"); // old numer - new number

        // then
        assertThat(stanislaw.getNumber()).hasValue(1189);
        assertThat(stanislaw.getOldNumber()).hasValue(953);

        final RegistryNumbers.Numbers witold = registryNumbers.parseLine("705  1538"); // old numer - new number

        // then
        assertThat(witold.getNumber()).hasValue(1538);
        assertThat(witold.getOldNumber()).hasValue(705);
    }
}
