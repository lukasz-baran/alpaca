package com.evolve.alpaca.importing;

import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ImportAndFixDataTest {

    @Autowired
    ImportDbfService importDbfService;

    @Test
    void importAndFix() throws IOException {
        final Resource resourcePersons = new DefaultResourceLoader().getResource("Z_B_KO.DBF");

        // we don't want this test to fail when there no files:
        assumeThat(resourcePersons.exists())
                .isTrue();

        importDbfService.startImport(resourcePersons.getFile().getPath(), "");

    }
}
