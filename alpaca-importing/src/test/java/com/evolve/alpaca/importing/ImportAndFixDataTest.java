package com.evolve.alpaca.importing;

import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.repo.jpa.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:test.properties")
@Slf4j
public class ImportAndFixDataTest {

    @Autowired
    ImportDbfService importDbfService;

    @Autowired
    PersonRepository personRepository;

    @Test
    void importAndFix() throws IOException {
        // given
        final Resource resourcePersons = new DefaultResourceLoader().getResource("Z_B_KO.DBF");
        final Resource resourceAccounts = new DefaultResourceLoader().getResource("PLAN.DBF");
        final Resource oldDoc = new DefaultResourceLoader().getResource("PLAN KONT.doc");

        // we don't want this test to fail when there is no DBF files:
        assumeThat(resourcePersons.exists() && resourceAccounts.exists() && oldDoc.exists())
                .isTrue();

        // when
        importDbfService.startImport(
                new ImportDataCommand(resourcePersons.getFile().getPath(),
                        resourceAccounts.getFile().getPath(),
                        oldDoc.getFile().getPath(),
                        mock(ImportDataCommand.ImportProgressListener.class)));

        // then
        sanityChecks(personRepository.findAll());
    }

    void sanityChecks(List<Person> importedPersons) {
        assertThat(importedPersons)
                .hasSizeGreaterThan(1000);

        assertPerson(getPersonById(importedPersons, "01003"))
                .hasStatus(PersonStatus.RESIGNED);

        assertPerson(getPersonById(importedPersons, "01021"))
                .hasStatus(PersonStatus.ACTIVE);
        // 01014 - rezygnacja 16.09.03

        assertPerson(getPersonById(importedPersons, "07123"))
                .isExemptFromFees();
        assertPerson(getPersonById(importedPersons, "02102"))
                .isExemptFromFees();

        // then -- check: "R 11.06.02" - resigned status
        assertPerson(getPersonById(importedPersons, "20015"))
                .hasStatus(PersonStatus.RESIGNED);

        assertPerson(getPersonById(importedPersons, "16150"))
                .hasNoSecondName();

    }

    Person getPersonById(List<Person> importedPersons, String personId) {
        return importedPersons.stream()
                .filter(person -> person.getPersonId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("The following person id is not found: " + personId));
    }
}
