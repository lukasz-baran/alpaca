package com.evolve.alpaca.importing.importDbf;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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
}
