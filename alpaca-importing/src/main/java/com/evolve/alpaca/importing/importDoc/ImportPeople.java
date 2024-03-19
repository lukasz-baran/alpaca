package com.evolve.alpaca.importing.importDoc;

import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.evolve.alpaca.importing.importDoc.person.PersonReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.FileInputStream;
import java.util.*;

@Slf4j
public class ImportPeople {
    private static final String INCORRECT_SEPARATOR = "–";
    private static final String CHUNK_SEPARATOR = "ALFABETYCZNIE";

    private final boolean logging;

    public ImportPeople(boolean logging) {
        this.logging = logging;
    }

    @SneakyThrows
    public List<PersonFromDoc> processDocFile(String filePath) {
        try (HWPFDocument hwpf = new HWPFDocument(new FileInputStream(filePath));
            WordExtractor wordExtractor = new WordExtractor(hwpf)) {
            final String rawContent = wordExtractor.getText();
            int index = StringUtils.indexOf(rawContent, CHUNK_SEPARATOR);

            final String content = StringUtils.truncate(rawContent, index);
            return processFile(content);
        }
    }

    private List<PersonFromDoc> processFile(String content) {
        final List<PersonFromDoc> personList = new ArrayList<>();
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
