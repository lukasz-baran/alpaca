package com.evolve.alpaca.importing.importDbf.fixers;

import com.evolve.alpaca.importing.DateParser;
import com.evolve.domain.Person;
import com.evolve.domain.PersonGenderDeducer;
import com.evolve.domain.PersonStatusChange;
import com.evolve.domain.RegistryNumber;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * PersonFixer is the last resort of engine that imports data from DBF files.
 */
@Component
@Slf4j
public class PersonFixer implements InitializingBean {
    public static final String FIRST_NAME_KEY = "firstName";
    public static final String LAST_NAME_KEY = "lastName";
    public static final String SECOND_NAME_KEY = "secondName";
    public static final String DOB_KEY = "dob";
    public static final String JOINED_DATE_KEY = "joinedDate";
    public static final String RESIGNED_DATE_KEY = "resignedDate";
    public static final String DEATH_DATE_KEY = "deathDate";
    public static final String REMOVED_DATE_KEY = "removedDate";
    public static final String PREVIOUS_NAME_KEY = "previousName";
    public static final String REGISTRY_NAME_KEY = "registryNumber";
    public static final String OLD_REGISTRY_NAME_KEY = "oldRegistryNumber";


    public static final Map<String, String> FIXER_TAGS_TO_TEXT = ImmutableMap.<String, String>builder()
                .put(FIRST_NAME_KEY, "Imię")
                .put(LAST_NAME_KEY, "Nazwisko")
                .put(SECOND_NAME_KEY, "Drugie imię")
                .put(DOB_KEY, "Data urodzenia")
                .put(JOINED_DATE_KEY, "Data dołączenia")
                .put(RESIGNED_DATE_KEY, "Data rezygnacji")
                .put(DEATH_DATE_KEY, "Data śmierci")
                .put(REMOVED_DATE_KEY, "Data skreślenia")
                .put(PREVIOUS_NAME_KEY, "Poprzednie nazwisko")
                .put(REGISTRY_NAME_KEY, "Numer kartoteki")
                .put(OLD_REGISTRY_NAME_KEY, "Numer starej kartoteki")
                .build();

    public static final String ID = "id";
    public static final String FIELD = "field";
    public static final String NEW_VALUE = "newValue";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Resource resource;
    private final Map<String, Map<String, String>> data = new HashMap<>();

    public PersonFixer(@Value("classpath:fixer.csv") Resource resource) {
        this.resource = resource;
    }

    @SneakyThrows
    public int loadData() {
        // it has to be input stream, not file because it is in jar file
        if (!resource.exists()) {
            return -1;
        }
        final InputStream csvData = resource.getInputStream();

        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setIgnoreEmptyLines(true)
                .setHeader("id", "field", "newValue")
                .build();

        try (CSVParser parser = CSVParser.parse(csvData, DEFAULT_CHARSET, format)) {
            final List<CSVRecord> csvRecords = parser.getRecords();
            csvRecords.forEach(csvRecord -> {
                    final String id = csvRecord.get(ID);
                    final String field = csvRecord.get(FIELD);
                    final String newValue = csvRecord.get(NEW_VALUE);
                    var map = data.computeIfAbsent(id, k -> new HashMap<>());
                    map.put(field, newValue);
            });
        }

        log.info("Data loaded from fixer: {}", data);
        return data.size();
    }

    @Override
    public void afterPropertiesSet() {
        loadData();
    }

    public Person fixData(Person person) {
        final String personId = person.getPersonId();
        getRecords(personId)
                .forEach((field, newValue) -> fix(person, field, newValue));
        return person;
    }

    public Map<String, String> getRecords(String personId) {
        if (data.containsKey(personId)) {
            return data.get(personId);
        }
        return Collections.emptyMap();
    }

    public void fix(Person person, String field, String newValue) {
        switch (field) {
            case FIRST_NAME_KEY -> setPersonFirstNameAndGender(person, newValue);
            case LAST_NAME_KEY -> person.setLastName(newValue);
            case SECOND_NAME_KEY -> person.setSecondName(newValue);
            case DOB_KEY -> DateParser.parse(newValue).ifPresent(dob -> addPersonDateOfBirth(person, dob));
            case JOINED_DATE_KEY -> DateParser.parse(newValue).ifPresent(doa -> addPersonJoinedDate(person, doa));
            case RESIGNED_DATE_KEY -> DateParser.parse(newValue).ifPresent(dor -> addPersonResignedDate(person, dor));
            case DEATH_DATE_KEY -> DateParser.parse(newValue).ifPresent(dateOfDeath -> addPersonDeathDate(person, dateOfDeath));
            case REMOVED_DATE_KEY -> DateParser.parse(newValue).ifPresent(dateRemoved -> addPersonRemovedDate(person, dateRemoved));
            case PREVIOUS_NAME_KEY -> addPreviousLastName(person, newValue);
            case REGISTRY_NAME_KEY -> person.setRegistryNumber(RegistryNumber.of(newValue));
            case OLD_REGISTRY_NAME_KEY -> person.setOldRegistryNumber(RegistryNumber.of(newValue));
            default -> {}
        }
    }

    public void setPersonFirstNameAndGender(Person person, String firstName) {
        person.setFirstName(firstName);
        person.setGender(PersonGenderDeducer.getGender(firstName));
    }

    public void addPersonJoinedDate(Person person, LocalDate joinedDate) {
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.JOINED, joinedDate);
    }

    public void addPersonResignedDate(Person person, LocalDate resignedDate) {
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.RESIGNED, resignedDate);
    }

    public void addPersonDateOfBirth(Person person, LocalDate actualPersonDob) {
        person.setDob(actualPersonDob);
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.BORN, actualPersonDob);
    }

    private void addPersonDeathDate(Person person, LocalDate dateOfDeath) {
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.DIED, dateOfDeath);
    }

    private void addPersonRemovedDate(Person person, LocalDate dateOfRemoved) {
        person.addOrUpdateStatusChange(PersonStatusChange.EventType.REMOVED, dateOfRemoved);
    }

    public void addPreviousLastName(Person person, String previousLastName) {
        if (person.getPreviousLastNames() == null) {
            final List<String> previousLastNames = new ArrayList<>();
            previousLastNames.add(previousLastName);
            person.setPreviousLastNames(previousLastNames);
        } else {
            person.getPreviousLastNames().add(previousLastName);
        }
    }

}
