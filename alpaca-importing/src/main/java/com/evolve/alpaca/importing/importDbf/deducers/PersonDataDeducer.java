package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.RegistryNumbers;
import com.evolve.alpaca.utils.DateUtils;
import com.evolve.domain.*;
import com.evolve.alpaca.importing.DateParser;
import com.evolve.alpaca.importing.importDbf.domain.DbfPerson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
public class PersonDataDeducer {
    private final DbfPerson person;
    private final IssuesLogger issuesLogger;
    private final RegistryNumbers registryNumbers;
    private List<String> guesses;

    public PersonDataDeducer(DbfPerson person, IssuesLogger issuesLogger, RegistryNumbers registryNumbers) {
        this.person = person;
        this.issuesLogger = issuesLogger;
        this.guesses = Lists.newArrayList(
                StringUtils.trim(person.getNAZ_ODB3()),
                StringUtils.trim(person.getNAZ_ODB4()),
                StringUtils.trim(person.getNAZ_ODB5()),
                StringUtils.trim(person.getNAZ_ODB6()),
                StringUtils.trim(person.getNAZ_ODB7()),
                StringUtils.trim(person.getINFO()));

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

        final AuthorizedPersonDeducer authorizedPersonDeducer = new AuthorizedPersonDeducer(issues);
        Optional<List<Person.AuthorizedPerson>> maybeAuthorizedPerson = authorizedPersonDeducer.deduceFrom(guesses);
        if (maybeAuthorizedPerson.isPresent()) {
            guesses = authorizedPersonDeducer.removeGuesses(guesses);
        }

        final Optional<LocalDate> maybeDob = new PersonDateOfBirthDeducer(issues).deduceFrom(guesses);
        final Optional<LocalDate> maybeJoiningDate = new JoiningDateDeducer(issues).deduceFrom(guesses);

        final List<Person.PersonAddress> personAddresses =
                maybeAddress.map(address -> new Person.PersonAddress(address, Person.AddressType.HOME))
                        .map(List::of)
                        .orElse(List.of());

        final List<Person.AuthorizedPerson> authorizedPeople =
                maybeAuthorizedPerson.orElse(List.of());

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

        final List<PersonStatusChange> statusChanges = deduceStatusChanges(maybeDob, maybeJoiningDate, personStatusDetails);

        final Person personData = Person.builder()
                .personId(personId.map(PersonId::toString).orElse(null))
                .firstName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getFirstName).orElse(null))
                .secondName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getSecondName).orElse(null))
                .gender(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getGender).orElse(null))
                .lastName(credentials.map(PersonCredentialsDeducer.DeducedCredentials::getLastName).orElse(null))
                .dob(maybeDob.orElse(null))
                .registryNumber(registryNumber.orElse(null))
                .oldRegistryNumber(oldRegistryNumber.orElse(null))
                .addresses(personAddresses)
                .authorizedPersons(authorizedPeople)
                .status(personStatusDetails.orElse(null))
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

    List<PersonStatusChange> deduceStatusChanges(Optional<LocalDate> maybeDob,
            Optional<LocalDate> maybeJoinedDate, Optional<PersonStatusDetails> personStatusDetails) {
        final List<PersonStatusChange> statusChanges = new ArrayList<>();
        maybeDob.ifPresent(dob -> statusChanges.add(PersonStatusChange.builder()
                        .eventType(PersonStatusChange.EventType.BORN)
                        .when(dob)
                .build()));

        maybeJoinedDate.ifPresent(joinedDate -> statusChanges.add(PersonStatusChange.builder()
                        .eventType(PersonStatusChange.EventType.JOINED)
                        .when(joinedDate)
                        .build()));

        personStatusDetails.ifPresent(statusDetails -> {
            switch (statusDetails.getStatus()) {
                case DEAD:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.DIED)
                            .when(tryParseDate(statusDetails.getDeathDate()).orElse(null))
                            .originalValue(statusDetails.getDeathDate())
                            .build());
                    break;
                case RESIGNED:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.RESIGNED)
                            .when(tryParseDate(statusDetails.getResignationDate()).orElse(null))
                            .originalValue(statusDetails.getResignationDate())
                            .build());
                    break;
                case REMOVED:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.REMOVED)
                            .when(tryParseDate(statusDetails.getRemovedDate()).orElse(null))
                            .originalValue(statusDetails.getRemovedDate())
                            .build());
                    break;
                case UNKNOWN:
                    break;
            }
        });
        return statusChanges;
    }

    private static Optional<LocalDate> tryParseDate(String date) {
        try {
            return DateParser.parse(date)
                    .map(DateUtils::adjustDateToCurrentCentury);
        } catch (DateTimeException dateTimeException) {
            return Optional.empty();
        }
    }
}
