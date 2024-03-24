package com.evolve.alpaca.importing.importDoc;

import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.group.PersonGroupReader;
import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.evolve.domain.Group;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 Lista niezbędnych kuracji danych:

 1) nazwiska panieńskie
 [FOO]

 2) kuracja podwójnych nazwisk
 FOO -BAR
 BAR- FOO

 3) data śmierci
 ZM 2,02,99
 ZM26.10.98
 ZM 9.08.97
 ZM

 4) rezygnacja
 REZ III-98
 rez
 rez.

 */
@Slf4j
@RequiredArgsConstructor
public class ImportAlphanumeric {
    private static final String CHUNK_SEPARATOR = "ALFABETYCZNIE";

    public static final List<String> START_SECTIONS = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "Ł", "M", "N", "O", "P", "R", "S", "Ś", "T", "U", "W", "Z", "Ż");

    private final boolean logging;

    @SneakyThrows
    public GrupyAlfabetyczne processDocFile(String filePath) {
        try (HWPFDocument hwpf = new HWPFDocument(new FileInputStream(filePath));
             WordExtractor wordExtractor = new WordExtractor(hwpf)) {
            final String rawContent = wordExtractor.getText();
            int index = StringUtils.indexOf(rawContent, CHUNK_SEPARATOR);

            final String content = StringUtils.substring(rawContent, index + CHUNK_SEPARATOR.length(), rawContent.length());
            return processFile(content);
        }
    }

    @SneakyThrows
    public GrupyAlfabetyczne processFile(String content) {
        final GrupyAlfabetyczne grupy = new GrupyAlfabetyczne();

        Group currentGroup = null;

        try (final Scanner scanner = new Scanner(content)) {
            while (scanner.hasNextLine()) {
                String line = curateLine(scanner.nextLine());
                if (skipLine(line)) {
                    continue;
                }

                System.out.println("line " +  line);

                if (groupStarter(line)) {
                    currentGroup = Group.groupFor(line.charAt(0))
                            .orElseThrow(() -> new RuntimeException("Cannot assign to group " + line));
                    log.warn("Detected group: {}", currentGroup);
                    continue;
                }

                final Optional<PersonFromDoc> person = PersonGroupReader.fromLine(line);
                person.ifPresentOrElse(System.out::println, () -> System.err.println("Unable to decode line: " + line));

                if (person.isPresent()) {
                     grupy.addNewPerson(currentGroup, person.get());
                }

            }
        }
        log.info(grupy.toString());
        grupy.validateContinuity(logging);
        return grupy;
    }

    boolean groupStarter(String line) {
        return line.equals("L Ł") ||
                (line.length() == 1 && START_SECTIONS.contains(line));
    }

    String curateLine(String line) {
        return line.replaceAll("\\[", " [");
    }

    /**
     * Skip line when:
     * - it's empty
     */
    boolean skipLine(String line) {
        return StringUtils.isBlank(line);
    }
}
