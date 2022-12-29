package com.evolve.importDbf.deducers;

import com.evolve.importDbf.DbfPerson;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
public class NamePersonDeducer implements SmartDeducer<NamePersonDeducer.DeducedCredentials> {
    private static final String SEPARATOR = " ";

    private final String nazwa1;
    private final String nazwa2;

    public NamePersonDeducer(DbfPerson dbfPerson) {
        this.nazwa1 = dbfPerson.getNAZ_ODB1().trim();
        this.nazwa2 = dbfPerson.getNAZ_ODB2().trim();
    }

    @Override
    public Optional<DeducedCredentials> deduceFrom(List<String> guesses) {
        final String[] first = this.nazwa1.split(SEPARATOR, 2);
        final String[] second = this.nazwa2.split(SEPARATOR, 2);

        if (StringUtils.equalsAnyIgnoreCase(first[0], second[0]) &&
                StringUtils.equalsAnyIgnoreCase(first[1], second[1])) {
            return Optional.of(new DeducedCredentials(
                    StringUtils.capitalize(first[1]),
                    StringUtils.capitalize(first[0])));
        }

        log.warn("Unable to get consistent first name and last name for {} and {}", nazwa1, nazwa2);
        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        // no need to remove anything
        return guesses;
    }

    // TODO deduce and compare names from the fields

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class DeducedCredentials {
        private final String firstName;
        private final String lastName;
    }


}
