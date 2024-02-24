package com.evolve;

import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.RegistryNumber;
import com.evolve.services.PersonApplicationService;
import com.evolve.services.PersonsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonLookupTest extends AlpacaAbstractIntegrationTest {

    @Autowired
    PersonsService personsService;
    @Autowired
    PersonApplicationService personApplicationService;

    @Test
    void shouldFilterPersons() {
        // given  -- initially database is empty
        assertThat(personsService.fetchList(PersonLookupCriteria.ALL)).isEmpty();

        insertPerson("foo", "bar", "01", PersonStatus.ACTIVE, Person.Gender.FEMALE, true, true, 1);
        insertPerson("qwe", "rty", "02", PersonStatus.ACTIVE, Person.Gender.MALE, false, true, null);
        insertPerson("abc", "def", "02", PersonStatus.ACTIVE, Person.Gender.MALE, null, false, 3);
        insertPerson("sop", "ter", "03", PersonStatus.RESIGNED, Person.Gender.FEMALE, true, null, 4);

        // when
        assertThat(personsService.fetchList(PersonLookupCriteria.ALL)).hasSize(4);

        fetchByUnitNumber();

        fetchByStatus();

        fetchByGender();

        fetchByRetired();

        fetchByExemptFromTaxes();

        fetchByRegistryNumber();

        verifySorting();
    }

    private void fetchByStatus() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .status(PersonStatus.ACTIVE)
                .build()))
                .hasSize(3);

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .status(PersonStatus.RESIGNED)
                .build()))
                .hasSize(1);

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .status(PersonStatus.ARCHIVED)
                .build()))
                .isEmpty();
    }


    private void fetchByGender() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .gender(Person.Gender.FEMALE)
                .build()))
                .hasSize(2);
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .gender(Person.Gender.MALE)
                .build()))
                .hasSize(2);
    }

    private void fetchByUnitNumber() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .unitNumber("02")
                .build()))
                .hasSize(2);

        // check non-existent unit number
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .unitNumber("00")
                .build()))
                .isEmpty();
    }

    private void fetchByRetired() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .retired(true)
                .build()))
                .hasSize(2);

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .retired(false)
                .build()))
                .hasSize(2);
    }

    private void fetchByExemptFromTaxes() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .exemptFromFees(true)
                .build()))
                .hasSize(2);

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .exemptFromFees(false)
                .build()))
                .hasSize(2);
    }

    private void fetchByRegistryNumber() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .registryNumber(3)
                .build()))
                .hasSize(1);
    }

    private void verifySorting() {
        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .sortBy("unitNumber")
                .upDown(false)
                .build()))
                .hasSize(4)
                .extracting(Person::getUnitNumber)
                .containsSequence("03", "02", "02", "01");

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .sortBy("unitNumber")
                .upDown(true)
                .build()))
                .hasSize(4)
                .extracting(Person::getUnitNumber)
                .containsSequence("01", "02", "02", "03");

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .sortBy("registryNumber")
                .upDown(true)
                .build()))
                .hasSize(4)
                .extracting(person -> Optional.ofNullable(person.getRegistryNumber()).map(RegistryNumber::getRegistryNum).orElse(null))
                .containsSequence(null, 1, 3, 4);

        assertThat(personsService.fetch(PersonLookupCriteria.builder()
                .sortBy("registryNumber")
                .upDown(false)
                .build()))
                .hasSize(4)
                .extracting(person -> Optional.ofNullable(person.getRegistryNumber()).map(RegistryNumber::getRegistryNum).orElse(null))
                .containsSequence(4, 3, 1, null);
    }

    private String insertPerson(String firstName, String lastName, String unitNumber,
                              PersonStatus status,
                              Person.Gender gender,
                              Boolean retired,
                              Boolean exemptFromFees,
                              Integer registryNumber) {
        final Optional<String> nextId = personsService.findNextPersonId(lastName);
        final String personId = nextId.orElseThrow();
        final Person newPerson = Person.builder()
                .personId(personId)
                .firstName(firstName)
                .lastName(lastName)
                .unitNumber(unitNumber)
                .status(status)
                .gender(gender)
                .retired(retired)
                .exemptFromFees(exemptFromFees)
                .registryNumber(RegistryNumber.of(registryNumber))
                .build();
        personApplicationService.insertPerson(newPerson);
        return personId;
    }

}
