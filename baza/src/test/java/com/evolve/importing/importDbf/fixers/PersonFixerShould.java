package com.evolve.importing.importDbf.fixers;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

class PersonFixerShould {

    @Test
    void loadFile() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("fixer.csv");


        PersonFixer personFixer = new PersonFixer(resource);

        personFixer.loadData();
    }

}