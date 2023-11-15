package com.evolve.alpaca.importing.importDbf.fixers;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

class PersonFixerShould {

    @Test
    void loadFile() {
        // given
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("fixer.csv");
        PersonFixer personFixer = new PersonFixer(resource);

        // when
        int result = personFixer.loadData();

        // then
        assertThat(result)
                .isGreaterThan(0);
    }

}