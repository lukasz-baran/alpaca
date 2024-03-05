package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.domain.PersonId;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
public class PersonIdDeducer implements SmartDeducer<PersonId> {
    private static final int PERSON_ID_LENGTH = 5;

    private static final String INVALID_PERSON_NUMBER = "07 15";

    private final String symbolOdb; // 01001

    public PersonIdDeducer(DbfPerson dbfPerson) {
        this.symbolOdb = trimToEmpty(dbfPerson.getSYM_ODB());
    }

    @Override
    public Optional<PersonId> deduceFrom(List<String> guesses) {
        if (symbolOdb.length() < PERSON_ID_LENGTH) {
            log.info("Skipping incorrect SYM_ODB: {}", symbolOdb);
            return Optional.empty();
        }

        // Ugly hack for one person whose person id is incorrectly written in the original DB:
        if (INVALID_PERSON_NUMBER.equals(symbolOdb)) {
            return Optional.of(PersonId.of("07150"));
        }

        return Optional.of(PersonId.of(symbolOdb));
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        return guesses;
    }
}
