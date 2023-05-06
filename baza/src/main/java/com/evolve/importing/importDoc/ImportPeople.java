package com.evolve.importing.importDoc;

import com.evolve.importing.importDoc.person.Person;
import com.evolve.importing.importDoc.person.PersonReader;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class ImportPeople {
    public static final String FILENAME_BY_NUMBERS = "ludzie-numerami.txt";
    public static final URL FILE_BY_NUMBERS = Resources.getResource(FILENAME_BY_NUMBERS);

    private static final String INCORRECT_SEPARATOR = "–";

    private final File fileByNumbers;
    private final boolean logging;

    public ImportPeople(boolean logging) {
        this.fileByNumbers = new File(FILE_BY_NUMBERS.getFile());
        this.logging = logging;
    }

    @SneakyThrows
    public List<Person> processFile() {
        final List<Person> personList = new ArrayList<>();
        try (final Scanner scanner = new Scanner(fileByNumbers)) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().replaceAll(INCORRECT_SEPARATOR, "-");
                if (skipLine(line)) {
                    continue;
                }

                Optional<Person> maybePerson = PersonReader.fromLine(line);

                if (maybePerson.isPresent()) {
                    Person person = maybePerson.get();

                    if (!person.isCorrect() && logging) {
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
