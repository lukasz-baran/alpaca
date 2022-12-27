package com.evolve.importDoc;

import com.evolve.importDoc.person.Person;
import com.evolve.importDoc.person.PersonReader;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

@Slf4j
public class ImportPeople {

    public static final URL FILE_BY_NUMBERS = Resources.getResource("ludzie-numerami.txt");
    private static final String INCORRECT_SEPARATOR = "–";

    @SneakyThrows
    public List<Person> processFile() {
        final List<Person> personList = new ArrayList<>();
        try (final Scanner scanner = new Scanner(new File(FILE_BY_NUMBERS.getFile()))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().replaceAll(INCORRECT_SEPARATOR, "-");
                if (skipLine(line)) {
                    continue;
                }

                Optional<Person> maybePerson = PersonReader.fromLine(line);

                if (maybePerson.isPresent()) {
                    Person person = maybePerson.get();

                    if (!person.isCorrect()) {
                        log.warn("incorrect: {}", person);
                    }

                    personList.add(person);
                } else {
                    log.warn("failed: {}", line);
                }
            }
        }
        return personList;
    }


    boolean skipLine(String line) {
        if (StringUtils.isBlank(line)) {
            return true;
        }

        if (line.startsWith("Wykaz")) {
            return true;
        }

        if (line.startsWith("PLAN")) {
            return true;
        }

        if (line.contains("URSZULA") && line.contains("STACHOWICZ")) {
            return true;
        }

        // skip remarks
        if (line.startsWith("(")) {
            return true;
        }

        return false;
    }



}
