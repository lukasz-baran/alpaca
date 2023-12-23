package com.evolve;

import com.evolve.domain.Person;
import com.evolve.domain.PersonAssertion;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.RegistryNumber;
import com.evolve.services.PersonEditService;
import com.evolve.services.PersonsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:test.properties")
public class AlpacaIntegrationTest {

    public static final String TEST_FIRST_NAME = "Jan";
    public static final String TEST_LAST_NAME = "Barabasz";
    public static final String TEST_UNIT_NAME = "02";

    public static final String NEW_FIRST_NAME = "Patrick";
    public static final String NEW_LAST_NAME = "Swayze";
    public static final String NEW_EMAIL = "patrick.swayze@none.com";
    public static final String NEW_REGISTRY_NUMBER = "1234";

    @Autowired PersonsService personsService;
    @Autowired PersonEditService personEditService;

    @Test
    void testApp() {
        // given  -- initially database is empty
        var list = personsService.fetchList(PersonLookupCriteria.ALL);
        assertThat(list)
                .isEmpty();

        // when
        final Optional<String> nextId = personsService.findNextPersonId("Barabasz");

        // then
        assertThat(nextId)
                .hasValue("02001");

        // when
        final String personId = nextId.orElseThrow();
        final Person newPerson = Person.builder()
                .personId(personId)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .unitNumber(TEST_UNIT_NAME)
                .build();
        personsService.insertPerson(newPerson);

        // then
        assertThat(personsService.fetch(PersonLookupCriteria.ALL), PersonAssertion.class)
                .hasSize(1)
                .first()
                .hasFirstName(TEST_FIRST_NAME)
                .hasLastName(TEST_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasPersonId(personId);

        assertPerson(personsService.findById(personId))
                .hasFirstName(TEST_FIRST_NAME)
                .hasLastName(TEST_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasPersonId(personId);

        // when -- person is edited
        personEditService.editPerson(new EditPersonDataCommand(personId, NEW_FIRST_NAME, NEW_LAST_NAME,
                null, NEW_EMAIL, List.of(), List.of(), List.of(), List.of(), TEST_UNIT_NAME, NEW_REGISTRY_NUMBER, null));

        // then -- changes are persisted in db
        assertPerson(personsService.findById(personId))
                .hasFirstName(NEW_FIRST_NAME)
                .hasLastName(NEW_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasEmail(NEW_EMAIL)
                .hasPersonId(personId)
                .hasRegistryNumber(RegistryNumber.of(NEW_REGISTRY_NUMBER));

        // then --
    }

}
