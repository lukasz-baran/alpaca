package com.evolve.importing.importDbf.deducers;

import com.evolve.importing.importDbf.DbfPerson;
import com.evolve.utils.StringFix;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
public class NamePersonDeducer extends
                AbstractSmartDeducer<NamePersonDeducer.DeducedCredentials> {
    private static final String SEPARATOR = " ";

    private final String nazwa1;
    private final String nazwa2;

    public NamePersonDeducer(DbfPerson dbfPerson, IssuesLogger.ImportIssues issues) {
        super(issues);
        this.nazwa1 = trimToEmpty(dbfPerson.getNAZ_ODB1());
        this.nazwa2 = trimToEmpty(dbfPerson.getNAZ_ODB2());
    }

    @Override
    public Optional<DeducedCredentials> deduceFrom(List<String> guesses) {
        final String[] first = this.nazwa1.split(SEPARATOR, 2);
        final String[] second = this.nazwa2.split(SEPARATOR, 2);
        if (first.length < 2 || second.length < 2) {
            return Optional.empty();
        }

        if (StringUtils.equalsAnyIgnoreCase(first[0], second[0]) &&
                StringUtils.equalsAnyIgnoreCase(first[1], second[1])) {
            return Optional.of(new DeducedCredentials(
                    StringFix.capitalize(first[1]),
                    StringFix.capitalize(first[0])));
        }

        final String logLine = String.format("Unable to get consistent first name and last name for %s and %s", nazwa1, nazwa2);
        issues.store(logLine);
        return Optional.empty();
    }

    @Override
    public List<String> removeGuesses(List<String> guesses) {
        // no need to remove anything
        return guesses;
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class DeducedCredentials {
        private final String firstName;
        private final String lastName;
    }


}
