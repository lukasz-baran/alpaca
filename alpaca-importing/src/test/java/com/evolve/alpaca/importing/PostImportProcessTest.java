package com.evolve.alpaca.importing;

import com.evolve.alpaca.importing.importDbf.PostImportStepService;
import com.evolve.alpaca.utils.TestUtils;
import com.evolve.repo.jpa.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:test.properties")
public class PostImportProcessTest {

    @Autowired PersonRepository personRepository;
    @Autowired PostImportStepService postImportStepService;

    @Test
    void decodePersonStatus() {
        assumeTrue(TestUtils.isOnLocalEnv());

        // "11022"
        var person = personRepository.findByPersonId("11022");

        postImportStepService.process(List.of(person));

    }

}
