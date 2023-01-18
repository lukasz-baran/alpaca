package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.Address;
import com.evolve.domain.Person;
import com.evolve.domain.PersonId;
import com.evolve.domain.PersonStatusDetails;
import com.evolve.importing.importDbf.DbfPerson;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class PersonDataDeducer {

    private final DbfPerson person;
    private final IssuesLogger issuesLogger;

    public Optional<Person> deduce() {
        List<String> guesses = Lists.newArrayList(
                StringUtils.trim(person.getNAZ_ODB3()),
                StringUtils.trim(person.getNAZ_ODB4()),
                StringUtils.trim(person.getNAZ_ODB5()),
                StringUtils.trim(person.getNAZ_ODB6()),
                StringUtils.trim(person.getNAZ_ODB7()));

        PersonIdDeducer personIdDeducer = new PersonIdDeducer(person);
        final Optional<PersonId> personId = personIdDeducer.deduceFrom(guesses);
        if (personId.isEmpty()) {
            log.warn("Unable to deduce ID from {}", person);
            return Optional.empty();
        }

        final IssuesLogger.ImportIssues issues = issuesLogger.forPersonId(personId.map(PersonId::toString).orElse(null));

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
        Optional<LocalDate> maybeDob = personDateOfBirthDeducer.deduceFrom(guesses);


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

        // ustalmy numery w kartotekach
        final RegistryNumbersDeducer registryNumbersDeducer = new RegistryNumbersDeducer(issues);
        final Optional<RegistryNumbersDeducer.RegistryNumber> registryNumbers =
                registryNumbersDeducer.deduceFrom(Lists.newArrayList(person.getNR_IDENT()));

        final Person personData = Person.builder()
                .personId(personId.map(PersonId::toString).orElse(null))
                .firstName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getFirstName).orElse(null))
                .secondName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getSecondName).orElse(null))
                .gender(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getGender).orElse(null))
                .lastName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getLastName).orElse(null))
                .dob(maybeDob.orElse(null))
                .registryNum(registryNumbers.flatMap(RegistryNumbersDeducer.RegistryNumber::getRegistryNum).orElse(null))
                .oldRegistryNum(registryNumbers.flatMap(RegistryNumbersDeducer.RegistryNumber::getOldRegistryNum).orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .status(personStatusDetails.orElse(null))
                .email(maybeEmail.orElse(null))
                .build();
        return Optional.of(personData);
    }

}
