package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.alpaca.importing.importDbf.person.DbfPerson;
import com.evolve.alpaca.utils.StringFix;
import com.evolve.domain.Person;
import com.evolve.domain.PersonGenderDeducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
public class PersonCredentialsDeducer extends
                AbstractSmartDeducer<PersonCredentialsDeducer.DeducedCredentials> {
    private static final String SEPARATOR = " ";

    private static final Map<String, String> NAMES_FIX = Map.ofEntries(
            Map.entry("Agnie", "Agnieszka"),
            Map.entry("Agnies", "Agnieszka"),
            Map.entry("Agniesz", "Agnieszka"),
            Map.entry("Agnieszk", "Agnieszka"),
            Map.entry("Aleksande", "Aleksander"),
            Map.entry("Aleksandr", "Aleksandra"),
            Map.entry("Alicj", "Alicja"),
            Map.entry("Barbar", "Barbara"),
            Map.entry("Beat", "Beata"),
            Map.entry("Bogu", "Bogumiła"),
            Map.entry("Domi", "Dominika"),
            Map.entry("Elż", "Elżbieta"),
            Map.entry("Elżbie", "Elżbieta"),
            Map.entry("Elżbiet", "Elżbieta"),
            Map.entry("Gabrie", "Gabriela"),
            Map.entry("Halin", "Halina"),
            Map.entry("Józe", "Józefa"),
            Map.entry("Julitt", "Julita"),
            Map.entry("Justy", "Justyna"),
            Map.entry("Karolin", "Karolina"),
            Map.entry("Kata", "Katarzyna"),
            Map.entry("Katar", "Katarzyna"),
            Map.entry("Katarz", "Katarzyna"),
            Map.entry("Katarzy", "Katarzyna"),
            Map.entry("Krysty", "Krystyna"),
            Map.entry("Krystyn", "Krystyna"),
            Map.entry("Krzy-fa", "Krzysztofa"),
            Map.entry("Małgor", "Małgorzata"),
            Map.entry("Małgorz", "Małgorzata"),
            Map.entry("Małgorza", "Małgorzata"),
            Map.entry("Małgorzat", "Małgorzata"),
            Map.entry("Mar", "Maria"),
            Map.entry("Mari", "Maria"),
            Map.entry("Monik", "Monika"),
            Map.entry("Pi0tr", "Piotr"),
            Map.entry("Stanis", "Stanisława"),
            Map.entry("Magdalen", "Magdalena"),
            Map.entry("Magdale", "Magdalena"));

    private final String nazwa1;
    private final String nazwa2;


    public PersonCredentialsDeducer(DbfPerson dbfPerson, IssuesLogger.ImportIssues issues) {
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

            // check double names
            if (StringUtils.contains(first[1], SEPARATOR)) {
                final String[] firstAndSecond = first[1].split(SEPARATOR);
                String firstName = firstAndSecond[0];
                String secondName = firstAndSecond[1];

                if (isPreviousName(firstName)) {
                    firstName = secondName;
                    secondName = null; // TODO we should extract previous name from the brackets
                }

                return Optional.of(new DeducedCredentials(
                        fixTruncatedFirstName(StringFix.capitalize(firstName)),
                        StringFix.capitalize(secondName),
                        StringFix.capitalizeLastName(first[0])));
            } else {
                final String firstName = first[1];

                return Optional.of(DeducedCredentials.onlyFirstAndLastName(
                        fixTruncatedFirstName(StringFix.capitalize(firstName)),
                        StringFix.capitalizeLastName(first[0])));
            }

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

    private static boolean isPreviousName(String maybePreviousName) {
        return StringUtils.startsWith(maybePreviousName, "(") && StringUtils.endsWith(maybePreviousName, ")");
    }

    private static String fixTruncatedFirstName(String firstName) {
        if (NAMES_FIX.containsKey(firstName)) {
            return NAMES_FIX.get(firstName);
        }
        return firstName;
    }

    public record DeducedCredentials(String firstName, String secondName, String lastName) {

        static DeducedCredentials onlyFirstAndLastName(String firstName, String lastName) {
            return new DeducedCredentials(firstName, null, lastName);
        }

        public Person.Gender getGender() {
            return PersonGenderDeducer.getGender(firstName);
        }

    }


}
