package com.evolve;

import com.evolve.domain.*;
import com.evolve.services.PersonApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

public class PersonEditionTest extends AlpacaAbstractIntegrationTest{

    public static final String TEST_FIRST_NAME = "Jan";
    public static final String TEST_LAST_NAME = "Barabasz";
    public static final String TEST_UNIT_NAME = "02";

    public static final String NEW_FIRST_NAME = "Patrick";
    public static final String NEW_LAST_NAME = "Swayze";
    public static final String NEW_EMAIL = "patrick.swayze@none.com";
    public static final String NEW_REGISTRY_NUMBER = "1234";

    @Autowired FindPerson findPerson;
    @Autowired PersonApplicationService personApplicationService;

    @Test
    void testApp() {
        // given  -- initially database is empty
        assertThat(findPerson.fetchList(PersonLookupCriteria.ALL)).isEmpty();

        // when
        final Optional<String> nextId = findPerson.findNextPersonId(TEST_LAST_NAME);

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
        personApplicationService.insertPerson(newPerson);

        // then
        assertThat(findPerson.fetch(PersonLookupCriteria.ALL), PersonAssertion.class)
                .hasSize(1)
                .first()
                .hasFirstName(TEST_FIRST_NAME)
                .hasLastName(TEST_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasPersonId(personId);

        assertPerson(findPerson.findById(personId))
                .hasFirstName(TEST_FIRST_NAME)
                .hasLastName(TEST_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasPersonId(personId);

        // when -- person is edited
        final BankAccount bankAccount = BankAccount.of("1234", "bank account description");

        personApplicationService.editPerson(new EditPersonDataCommand(personId, NEW_FIRST_NAME, NEW_LAST_NAME,
                null, List.of(PersonContactData.email(NEW_EMAIL)), List.of(), List.of(), List.of(), TEST_UNIT_NAME, NEW_REGISTRY_NUMBER, null,
                List.of(bankAccount), null, null, null, null));

        // then -- changes are persisted in db
        assertPerson(findPerson.findById(personId))
                .hasFirstName(NEW_FIRST_NAME)
                .hasLastName(NEW_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasEmail(NEW_EMAIL)
                .hasPersonId(personId)
                .hasRegistryNumber(RegistryNumber.of(NEW_REGISTRY_NUMBER))
                .hasBankAccount(bankAccount);

        // then --
    }

    @Test
    void shouldUpdateStatusWhenDobAndJoinedDateAdded() {
        // given -- person that doesn't have any statuses
        final String personId = findPerson.findNextPersonId(TEST_LAST_NAME).orElseThrow();
        final Person newPerson = Person.builder()
                .personId(personId)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .unitNumber(TEST_UNIT_NAME)
                .status(PersonStatus.UNKNOWN)
                .retired(true)
                .exemptFromFees(true)
                .build();
        personApplicationService.insertPerson(newPerson);

        assertPerson(findPerson.findById(personId))
                .hasStatus(PersonStatus.UNKNOWN)
                .hasNoBirthDate();

        // when -- statuses are added
        final LocalDate dob = LocalDate.of(1980, 8, 28);
        final LocalDate joined = LocalDate.of(2020, 1, 25);

        personApplicationService.editPerson(new EditPersonDataCommand(personId, TEST_FIRST_NAME, TEST_LAST_NAME,
                null, List.of(), List.of(), List.of(), statusChanges(dob, joined), TEST_UNIT_NAME, null, null,
                List.of(), null, null, null, null));

        // then -- person became active
        assertPerson(findPerson.findById(personId))
                .hasFirstName(TEST_FIRST_NAME)
                .hasLastName(TEST_LAST_NAME)
                .hasUnitNumber(TEST_UNIT_NAME)
                .hasPersonId(personId)
                .wasBornOn(dob)
                .hasStatus(PersonStatus.ACTIVE)
                .isRetired()
                .isExemptFromFees();


        // when -- dob is changed
        final LocalDate newDob = LocalDate.of(1970, 2, 11);
        personApplicationService.editPerson(new EditPersonDataCommand(personId, TEST_FIRST_NAME, TEST_LAST_NAME,
                null, List.of(), List.of(), List.of(), statusChanges(newDob, joined), TEST_UNIT_NAME, null, null,
                List.of(), null, null, null, null));


        // then -- changes are reflected
        assertPerson(findPerson.findById(personId))
                .wasBornOn(newDob)
                .hasStatus(PersonStatus.ACTIVE)
                .isRetired()
                .isExemptFromFees();


    }

    @Test
    void shouldKeepStatusWhenNewDateIsEarlier() {
        // given
        final String personId = findPerson.findNextPersonId(TEST_LAST_NAME).orElseThrow();
        final Person newPerson = Person.builder()
                .personId(personId)
                .firstName(TEST_FIRST_NAME)
                .lastName(TEST_LAST_NAME)
                .unitNumber(TEST_UNIT_NAME)
                .status(PersonStatus.UNKNOWN)
                .build();
        personApplicationService.insertPerson(newPerson);

        // when
        final LocalDate dob = LocalDate.of(1980, 8, 28);
        final LocalDate death = LocalDate.of(2020, 1, 25);
        var bornAndDied = List.of(PersonStatusChange.born(dob), PersonStatusChange.died(death));
        personApplicationService.editPerson(new EditPersonDataCommand(personId, TEST_FIRST_NAME, TEST_LAST_NAME,
                null, List.of(), List.of(), List.of(), bornAndDied, TEST_UNIT_NAME, null, null,
                List.of(), null, null, null, null));

        // then
        assertPerson(findPerson.findById(personId))
                .hasStatus(PersonStatus.DEAD)
                .wasBornOn(dob)
                .hasStatusHistory(bornAndDied);

        // when
        final LocalDate joined = LocalDate.of(2000, 1, 1);
        var bornJoinedAndDied = List.of(PersonStatusChange.born(dob), PersonStatusChange.died(death), PersonStatusChange.joined(joined));
        personApplicationService.editPerson(new EditPersonDataCommand(personId, TEST_FIRST_NAME, TEST_LAST_NAME,
                null, List.of(), List.of(), List.of(), bornJoinedAndDied, TEST_UNIT_NAME, null, null,
                List.of(), null, null, null, null));

        // then
        assertPerson(findPerson.findById(personId))
                .hasStatus(PersonStatus.DEAD)
                .wasBornOn(dob)
                .hasStatusHistory(PersonStatusChange.born(dob), PersonStatusChange.joined(joined), PersonStatusChange.died(death));
    }

    private List<PersonStatusChange> statusChanges(LocalDate dob, LocalDate joined) {
        return List.of(PersonStatusChange.born(dob), PersonStatusChange.joined(joined));
    }

}
