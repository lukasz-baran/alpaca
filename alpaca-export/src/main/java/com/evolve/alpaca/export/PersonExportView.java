package com.evolve.alpaca.export;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@AllArgsConstructor

public class PersonExportView {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final String ID = "ID";
    public static final String FIRST_NAME = "Imię";
    public static final String LAST_NAME = "Nazwisko";
    public static final String GENDER = "Płeć";
    public static final String STATUS = "Status";
    public static final String DOB = "Data urodzenia";
    public static final String AGE = "Wiek";
    public static final String ADDRESS_STREET = "Ulica";
    public static final String ADDRESS_POSTAL_CODE = "Kod pocztowy";
    public static final String ADDRESS_CITY = "Miasto";

    @CsvBindByName(column = ID)
    @CsvBindByPosition(position = 0)
    private final String id;
    @CsvBindByName(column = FIRST_NAME)
    @CsvBindByPosition(position = 1)
    private final String firstName;
    @CsvBindByName(column = LAST_NAME)
    @CsvBindByPosition(position = 2)
    private final String lastName;
    @CsvBindByName(column = GENDER)
    @CsvBindByPosition(position = 3)
    private final String gender;
    @CsvBindByName(column = STATUS)
    @CsvBindByPosition(position = 4)
    private final String status;
    @CsvBindByName(column = DOB)
    @CsvBindByPosition(position = 5)
    private final String dob;
    @CsvBindByName(column = AGE)
    @CsvBindByPosition(position = 6)
    private final String age;

    @CsvBindByName(column = ADDRESS_STREET)
    @CsvBindByPosition(position = 7)
    private final String addressStreet;

    @CsvBindByName(column = ADDRESS_POSTAL_CODE)
    @CsvBindByPosition(position = 8)
    private final String addressPostalCode;

    @CsvBindByName(column = ADDRESS_CITY)
    @CsvBindByPosition(position = 9)
    private final String addressCity;


    public static PersonExportView of(Person person) {
        return new PersonExportView(person.getPersonId(),
                person.getFirstName(),
                person.getLastName(),
                fetchGender(person),
                fetchStatus(person),
                fetchDob(person),
                calculateAge(person),
                fetchStreet(person),
                fetchCode(person),
                fetchCity(person));
    }

    private static String fetchStreet(Person person) {
        return fetchFirstAddress(person)
                .map(Person.PersonAddress::getStreet).orElse(null);
    }

    private static String fetchCode(Person person) {
        return fetchFirstAddress(person)
                .map(Person.PersonAddress::getPostalCode).orElse(null);
    }

    private static String fetchCity(Person person) {
        return fetchFirstAddress(person)
                .map(Person.PersonAddress::getCity).orElse(null);
    }

    private static Optional<Person.PersonAddress> fetchFirstAddress(Person person) {
        return emptyIfNull(person.getAddresses())
                .stream()
                .findFirst();
    }

    private static String fetchDob(Person person) {
        return Optional.ofNullable(person.getDob()).map(DATE_FORMATTER::format).orElse("");
    }

    private static String fetchGender(Person person) {
        return Optional.ofNullable(person.getGender()).map(Person.Gender::toString).orElse(null);
    }

    private static String fetchStatus(Person person) {
        return Optional.ofNullable(person.getStatus()).map(PersonStatus::toString).orElse(null);
    }

    private static String calculateAge(Person person) {
        return Optional.ofNullable(Person.calculateAge(person.getDob(), person.getStatus()))
                .filter(age -> age > 0)
                .map(Object::toString).orElse(null);
    }

}
