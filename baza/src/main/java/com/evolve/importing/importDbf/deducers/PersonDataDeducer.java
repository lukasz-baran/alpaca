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

        final NamePersonDeducer namePersonDeducer = new NamePersonDeducer(person, issues);
        final Optional<NamePersonDeducer.DeducedCredentials> credentials = namePersonDeducer.deduceFrom(guesses);

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

        final Person personData = Person.builder()
                .personId(personId.map(PersonId::toString).orElse(null))
                .firstName(credentials.map(NamePersonDeducer.DeducedCredentials::getFirstName).orElse(null))
                .lastName(credentials.map(NamePersonDeducer.DeducedCredentials::getLastName).orElse(null))
                .dob(maybeDob.orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .status(personStatusDetails.orElse(null))
                .build();
        return Optional.of(personData);
    }

}
