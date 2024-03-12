package com.evolve.alpaca.importing.importDoc;

import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.evolve.alpaca.importing.importDoc.person.PersonReader;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.*;

@Slf4j
public class ImportPeople {
    public static final String FILENAME_BY_NUMBERS = "ludzie-numerami.txt";

    private static final String INCORRECT_SEPARATOR = "–";

    private final boolean logging;

    public ImportPeople(boolean logging) {
        this.logging = logging;
    }

    @SneakyThrows
    public List<PersonFromDoc> processFile() {
        final List<PersonFromDoc> personList = new ArrayList<>();
        final URL url = Resources.getResource(FILENAME_BY_NUMBERS);
        final String content = Resources.toString(url, Charsets.UTF_8);
        try (final Scanner scanner = new Scanner(content)) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().replaceAll(INCORRECT_SEPARATOR, "-");
                if (skipLine(line)) {
                    continue;
                }

                Optional<PersonFromDoc> maybePerson;
                try {
                    maybePerson = PersonReader.fromLine(line);
                } catch (NoSuchElementException nseEx) {
                    log.error("Failed on parsing line: " + line);
                    throw nseEx;
                }

                if (maybePerson.isPresent()) {
                    PersonFromDoc person = maybePerson.get();

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
