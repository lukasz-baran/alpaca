package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.PersonStatusDetails;
import com.evolve.alpaca.importing.importDbf.RegistryNumbers;
import com.evolve.alpaca.importing.importDbf.deducers.status.JoiningDateDeducer;
import com.evolve.alpaca.importing.importDbf.deducers.status.PersonDateOfBirthDeducer;
import com.evolve.alpaca.importing.importDbf.deducers.status.StatusChangesHistoryBuilder;
import com.evolve.alpaca.importing.importDbf.deducers.status.StatusPersonDeducer;
import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.domain.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class PersonDataDeducer {
    private final DbfPerson person;
    private final IssuesLogger issuesLogger;
    private final RegistryNumbers registryNumbers;
    private final List<String> guesses;

    public PersonDataDeducer(DbfPerson person, IssuesLogger issuesLogger, RegistryNumbers registryNumbers) {
        this.person = person;
        this.issuesLogger = issuesLogger;
        this.guesses = Lists.newArrayList(
                StringUtils.trim(person.getNAZ_ODB3()),
                StringUtils.trim(person.getNAZ_ODB4()),
                StringUtils.trim(person.getNAZ_ODB5()),
                StringUtils.trim(person.getNAZ_ODB6()),
                StringUtils.trim(person.getNAZ_ODB7()),
                StringUtils.trim(person.getINFO()))
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.registryNumbers = registryNumbers;
    }

    public Optional<Person> deduce() {
        final Optional<PersonId> personId = new PersonIdDeducer(person).deduceFrom(guesses);
        final IssuesLogger.ImportIssues issues = issuesLogger.forPersonId(personId.map(PersonId::toString).orElse(null));
        if (personId.isEmpty()) {
            log.warn("Unable to deduce ID from {}", person);
            return Optional.empty();
        }

        final PersonCredentialsDeducer namePersonDeducer = new PersonCredentialsDeducer(person, issues);
        final Optional<PersonCredentialsDeducer.DeducedCredentials> credentials = namePersonDeducer.deduceFrom(guesses);

        // adres osoby
        final Optional<Address> maybeAddress = new SmartAddressPersonDeducer(issues, true).deduceFrom(guesses);

        // osoby upoważnione
        final List<Person.AuthorizedPerson> authorizedPeople =
                        new AuthorizedPersonDeducer(issues, true).deduceFrom(guesses)
                        .orElse(List.of());

        final Optional<PersonStatusChange> maybeDob = new PersonDateOfBirthDeducer(issues).deduceFrom(guesses);
        final Optional<PersonStatusChange> maybeJoiningDate = new JoiningDateDeducer(issues).deduceFrom(guesses);

        final List<Person.PersonAddress> personAddresses =
                maybeAddress.map(address -> new Person.PersonAddress(address, Person.AddressType.HOME))
                        .map(List::of)
                        .orElse(List.of());

        final Optional<PersonStatusDetails> personStatusDetails = new StatusPersonDeducer().deduceFrom(guesses);

        // telefony i emaile
        final List<PersonContactData> contactData = deducePhonesAndEmails(issues);

        // ustalmy numery w kartotekach
        final RegistryNumbers.Numbers numbers = registryNumbers.parseLine(person.getNR_IDENT());
        final Optional<RegistryNumber> registryNumber = numbers.getNumber().map(RegistryNumber::of);
        final Optional<RegistryNumber> oldRegistryNumber = numbers.getOldNumber().map(RegistryNumber::of);

        // numer jednostki
        final UnitNumberDeducer unitNumberDeducer = new UnitNumberDeducer(issues);
        final Optional<String> unitNumber = unitNumberDeducer.deduceFrom(Lists.newArrayList(person.getKONTO_WNP()));

        // konto bankowe
        final List<BankAccount> bankAccounts = deduceBankAccount(issues);

        final List<PersonStatusChange> statusChanges =
                StatusChangesHistoryBuilder.deduceStatusChanges(maybeDob, maybeJoiningDate, personStatusDetails);

        final PersonStatus personStatus = personStatusDetails.map(PersonStatusDetails::getStatus)
                .orElse(null);

        final Person personData = Person.builder()
                .personId(personId.map(PersonId::toString).orElse(null))
                .firstName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::firstName).orElse(null))
                .secondName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::secondName).orElse(null))
                .gender(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getGender).orElse(null))
                .lastName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::lastName).orElse(null))
                .dob(maybeDob.map(PersonStatusChange::getWhen).orElse(null))
                .registryNumber(registryNumber.orElse(null))
                .oldRegistryNumber(oldRegistryNumber.orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .status(personStatus)
                .statusChanges(statusChanges)
                .contactData(contactData)
                .unitNumber(unitNumber.orElse(null))
                .rawData(person.getData())
                .bankAccounts(bankAccounts)
                .build();
        return Optional.of(personData);
    }

    List<PersonContactData> deducePhonesAndEmails(final IssuesLogger.ImportIssues issues) {
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

        final List<PersonContactData> result = new ArrayList<>();

        maybePhoneNumbers.ifPresent(phoneNumbers -> {
            phoneNumbers.forEach(phoneNumber -> {
                result.add(new PersonContactData(phoneNumber, PersonContactData.ContactType.PHONE));
            });
        });

        maybeEmail.ifPresent(email ->
                result.add(new PersonContactData(email, PersonContactData.ContactType.EMAIL)));

        return result;
    }

    List<BankAccount> deduceBankAccount(final IssuesLogger.ImportIssues issues) {
        final BankAccountDeducer bankAccountDeducer = new BankAccountDeducer(issues);
        final Optional<BankAccount> bankAccount = bankAccountDeducer.deduceFrom(
                Stream.of(person.getBANK0(),
                                person.getBANK1(),
                                person.getBANK2(),
                                person.getBANK3(),
                                person.getBANK4())
                        .filter(Objects::nonNull)
                        .toList());
        return bankAccount.stream().toList();
    }

}
