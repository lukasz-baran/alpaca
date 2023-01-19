package com.evolve.importing.importDbf.fixers;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

class PersonFixerShould {

    @Test
    void loadFile() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("fixer.csv");


        PersonFixer personFixer = new PersonFixer(resource);

        personFixer.loadData();
    }

}