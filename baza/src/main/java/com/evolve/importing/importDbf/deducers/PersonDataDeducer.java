package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.*;
import com.evolve.importing.importDbf.DbfPerson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
public class PersonDataDeducer {
    private final DbfPerson person;
    private final IssuesLogger issuesLogger;
    private List<String> guesses;

    public PersonDataDeducer(DbfPerson person, IssuesLogger issuesLogger) {
        this.person = person;
        this.issuesLogger = issuesLogger;
        this.guesses = Lists.newArrayList(
                StringUtils.trim(person.getNAZ_ODB3()),
                StringUtils.trim(person.getNAZ_ODB4()),
                StringUtils.trim(person.getNAZ_ODB5()),
                StringUtils.trim(person.getNAZ_ODB6()),
                StringUtils.trim(person.getNAZ_ODB7()));

    }

    public Optional<Person> deduce() {
        PersonIdDeducer personIdDeducer = new PersonIdDeducer(person);
        final Optional<PersonId> personId = personIdDeducer.deduceFrom(guesses);
        final IssuesLogger.ImportIssues issues = issuesLogger.forPersonId(personId.map(PersonId::toString).orElse(null));
        if (personId.isEmpty()) {
            log.warn("Unable to deduce ID from {}", person);
            return Optional.empty();
        }

        final PersonCredentialsDeducer namePersonDeducer = new PersonCredentialsDeducer(person, issues);
        final Optional<PersonCredentialsDeducer.DeducedCredentials> credentials = namePersonDeducer.deduceFrom(guesses);

        final SmartAddressPersonDeducer addressDeducer = new SmartAddressPersonDeducer(issues);
        Optional<Address> maybeAddress = addressDeducer.deduceFrom(guesses);
        if (maybeAddress.isPresent()) {
            guesses = addressDeducer.removeGuesses(guesses);
        }

        final AuthorizedPersonDeducer authorizedPersonDeducer = new AuthorizedPersonDeducer(issues);
        Optional<List<Person.AuthorizedPerson>> maybeAuthorizedPerson = authorizedPersonDeducer.deduceFrom(guesses);
        if (maybeAuthorizedPerson.isPresent()) {
            guesses = authorizedPersonDeducer.removeGuesses(guesses);
        }

        final PersonDateOfBirthDeducer personDateOfBirthDeducer = new PersonDateOfBirthDeducer(issues);
        final Optional<LocalDate> maybeDob = personDateOfBirthDeducer.deduceFrom(guesses);

        final JoiningDateDeducer joiningDateDeducer = new JoiningDateDeducer(issues);
        final Optional<LocalDate> maybeJoiningDate = joiningDateDeducer.deduceFrom(guesses);

        final List<Person.PersonAddress> personAddresses =
                maybeAddress.map(address -> new Person.PersonAddress(address, Person.AddressType.HOME))
                        .map(List::of)
                        .orElse(List.of());

        final List<Person.AuthorizedPerson> authorizedPeople =
                maybeAuthorizedPerson.orElse(List.of());

        final StatusPersonDeducer statusPersonDeducer = new StatusPersonDeducer();
        Optional<PersonStatusDetails> personStatusDetails = statusPersonDeducer.deduceFrom(guesses);


        List<String> infoGuesses = Lists.newArrayList(
                StringUtils.trim(person.getEMAIL()),
                StringUtils.trim(person.getINFO()),
                StringUtils.trim(person.getTEL0()),
                StringUtils.trim(person.getTEL1()));

        final EmailPersonDeducer emailPersonDeducer = new EmailPersonDeducer(issues);
        final Optional<String> maybeEmail = emailPersonDeducer.deduceFrom(infoGuesses);
        if (maybeEmail.isPresent()) {
            infoGuesses = emailPersonDeducer.removeGuesses(infoGuesses);
        }

        final PhoneNumbersDeducer phoneNumbersDeducer = new PhoneNumbersDeducer(issues);
        final Optional<List<String>> maybePhoneNumbers = phoneNumbersDeducer.deduceFrom(Lists.newArrayList(person.getTEL0(), person.getTEL1()));

        // ustalmy numery w kartotekach
        final RegistryNumbersDeducer registryNumbersDeducer = new RegistryNumbersDeducer(issues);
        final Optional<RegistryNumber> registryNumbers =
                registryNumbersDeducer.deduceFrom(Lists.newArrayList(person.getNR_IDENT()));

        // numer jednostki
        final UnitNumberDeducer unitNumberDeducer = new UnitNumberDeducer(issues);
        final Optional<String> unitNumber = unitNumberDeducer.deduceFrom(Lists.newArrayList(person.getKONTO_WNP()));

        final List<PersonStatusChange> statusChanges = deduceStatusChanges(maybeDob, maybeJoiningDate);

        final Person personData = Person.builder()
                .personId(personId.map(PersonId::toString).orElse(null))
                .firstName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getFirstName).orElse(null))
                .secondName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getSecondName).orElse(null))
                .gender(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getGender).orElse(null))
                .lastName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getLastName).orElse(null))
                .dob(maybeDob.orElse(null))
                .registryNumber(registryNumbers.orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .status(personStatusDetails.orElse(null))
                .statusChanges(statusChanges)
                .email(maybeEmail.orElse(null))
                .phoneNumbers(maybePhoneNumbers.orElse(List.of()))
                .unitNumber(unitNumber.orElse(null))
                .rawData(person.getData())
                .build();
        return Optional.of(personData);
    }

    List<PersonStatusChange> deduceStatusChanges(Optional<LocalDate> maybeDob, Optional<LocalDate> maybeJoinedDate) {
        final List<PersonStatusChange> statusChanges = new ArrayList<>();
        maybeDob.ifPresent(dob -> statusChanges.add(PersonStatusChange.builder()
                        .eventType(PersonStatusChange.EventType.BORN)
                        .when(dob)
                .build()));

        maybeJoinedDate.ifPresent(joinedDate -> statusChanges.add(PersonStatusChange.builder()
                        .eventType(PersonStatusChange.EventType.JOINED)
                        .when(joinedDate)
                        .build()));

//        Optional.ofNullable(person)
//                .map(DbfPerson::getDATA_ZAL)
//                .ifPresent(accountCreationDate -> statusChanges.add(PersonStatusChange.builder()
//                                .eventType(PersonStatusChange.EventType.ACCOUNT_CREATED)
//                                .when(DateUtils.convertToLocalDateViaMilisecond(accountCreationDate))
//                        .build()));
        //person.getDATA_ZAL()

        return statusChanges;
    }

}
