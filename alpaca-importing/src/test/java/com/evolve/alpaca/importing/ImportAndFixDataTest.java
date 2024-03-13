package com.evolve.alpaca.importing;

import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import com.evolve.alpaca.importing.importDbf.account.ImportAccountDbf;
import com.evolve.alpaca.importing.importDbf.person.ImportPersonDbf;
import com.evolve.alpaca.importing.importDbf.turnover.ImportTurnoverDbf;
import com.evolve.alpaca.importing.importDoc.ImportAlphanumeric;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.repo.jpa.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.evolve.alpaca.importing.importDoc.ImportAlphanumeric.FILENAME_BY_ALPHA;
import static com.evolve.alpaca.importing.importDoc.ImportPeople.FILENAME_BY_NUMBERS;
import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

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
    @Disabled
    void importTurnovers() throws IOException {
        var turnoversFile = new DefaultResourceLoader().getResource("OBROTY.DBF").getURL();

        var turnovers = ImportTurnoverDbf.performImport(turnoversFile);

        turnovers.forEach(System.out::println);
    }

    @Test
    @Disabled
    void importAccounts() throws IOException {
        var accountsFile = new DefaultResourceLoader().getResource("PLAN.DBF").getURL();

        ImportAccountDbf.importAccounts(accountsFile);
    }

    @Test
    void importTextFile() {
        final Resource resourceTextFile = new DefaultResourceLoader().getResource(FILENAME_BY_NUMBERS);
        assumeThat(resourceTextFile.exists()).isTrue();

        final List<PersonFromDoc> people = new ImportPeople(true).processFile();
        assertThat(people)
                .hasSize(2368);
        log.info("Wczytano " + people.size() + " z indeksu " + FILENAME_BY_NUMBERS);
    }

    @Test
    @Disabled
    void importPeople() throws IOException {
        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();

        final URL accountsFile = new DefaultResourceLoader().getResource("Z_B_KO.DBF").getURL();

        final List<com.evolve.domain.Person> persons = ImportPersonDbf.importPeople(accountsFile);

        log.info("Wczytano " + grupyAlfabetyczne.getSize() + " z grup alfabetycznych " + FILENAME_BY_ALPHA);
        log.info("Wczytano " + persons.size());
    }

    @Test
    void importAndFix() throws IOException {
        // given
        final Resource resourcePersons = new DefaultResourceLoader().getResource("Z_B_KO.DBF");
        final Resource resourceAccounts = new DefaultResourceLoader().getResource("PLAN.DBF");
        // we don't want this test to fail when there is no DBF files:
        assumeThat(resourcePersons.exists() && resourceAccounts.exists())
                .isTrue();

        // when
        importDbfService.startImport(resourcePersons.getFile().getPath(), resourceAccounts.getFile().getPath());

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
