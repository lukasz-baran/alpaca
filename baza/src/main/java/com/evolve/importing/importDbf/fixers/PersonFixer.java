package com.evolve.importing.importDbf.fixers;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;
import com.evolve.importing.DateParser;
import com.evolve.importing.importDbf.deducers.PersonGenderDeducer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class PersonFixer implements InitializingBean {
    public static final String ID = "id";
    public static final String FIELD = "field";
    public static final String NEW_VALUE = "newValue";

    private final Resource resource;
    private final Map<String, Map<String, String>> data = new HashMap<>();

    public PersonFixer(@Value("classpath:fixer.csv") Resource resource) {
        this.resource = resource;
    }

    @SneakyThrows
    public void loadData() {
        final File csvData = resource.getFile();
        final CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setIgnoreEmptyLines(true)
                .setHeader("id", "field", "newValue")
                .build();

        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), format)) {
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
    }

    @Override
    public void afterPropertiesSet() {
        loadData();
    }

    public Person fixData(Person person) {
        final String personId = person.getPersonId();
        if (data.containsKey(personId)) {
            final Map<String, String> record = data.get(personId);
            record.forEach((field, newValue) -> fix(person, field, newValue));
        }
        return person;
    }

    public void fix(Person person, String field, String newValue) {
        switch (field) {
            case "firstName" -> setPersonFirstNameAndGender(person, newValue);
            case "lastName" -> person.setLastName(newValue);
            case "secondName" -> person.setSecondName(newValue);
            case "dob" -> DateParser.parse(newValue).ifPresent(dob -> addPersonDob(person, dob));
            case "previousName" -> addPreviousLastName(person, newValue);
            default -> {}
        }
    }

    public void setPersonFirstNameAndGender(Person person, String firstName) {
        person.setFirstName(firstName);
        person.setGender(PersonGenderDeducer.getGender(firstName));
    }

    public void addPersonDob(Person person, LocalDate actualPersonDob) {
        person.setDob(actualPersonDob);

        if (person.getStatusChanges() == null) {
            person.setStatusChanges(new ArrayList<>());
        }

        final Optional<PersonStatusChange> personStatusChangeDob =
            person.getStatusChanges()
                .stream()
                .filter(personStatusChange -> personStatusChange.getEventType() == PersonStatusChange.EventType.BORN)
                .findFirst();
        personStatusChangeDob
                .ifPresentOrElse(statusChange -> {
                    statusChange.setWhen(actualPersonDob);
                    statusChange.setEventType(PersonStatusChange.EventType.BORN);
                },
                () -> person.getStatusChanges().add(PersonStatusChange.builder()
                                .eventType(PersonStatusChange.EventType.BORN)
                                .when(actualPersonDob)
                        .build()));
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
