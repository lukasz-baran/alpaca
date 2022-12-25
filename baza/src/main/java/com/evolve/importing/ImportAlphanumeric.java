package com.evolve.importing;

import com.evolve.importing.group.Grupa;
import com.evolve.importing.group.GrupyAlfabetyczne;
import com.evolve.importing.group.PersonGroupReader;
import com.evolve.importing.person.Person;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 Lista niezbędnych kuracji danych:

 1) nazwiska panieńskie
 [MAZUREK]

 2) kuracja podwójnych nazwisk
 SZELIGA -NANASZKO
 NĘDZA- GAJDA

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
@RequiredArgsConstructor
public class ImportAlphanumeric {
    private final File fileAlphanumeric;

    public static final List<String> START_SECTIONS = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "Ł", "M", "N", "O", "P", "R", "S", "Ś", "T", "U", "W", "Z", "Ż");

    @SneakyThrows
    public GrupyAlfabetyczne processFile() {
        final GrupyAlfabetyczne grupy = new GrupyAlfabetyczne();

        Grupa currentGroup = null;

        try (final Scanner scanner = new Scanner(fileAlphanumeric)) {
            while (scanner.hasNextLine()) {
                final String line = curateLine(scanner.nextLine());
                if (skipLine(line)) {
                    continue;
                }

                if (groupStarter(line)) {
                    currentGroup = Grupa.groupFor(line.charAt(0))
                            .orElseThrow(() -> new RuntimeException("Cannot assign to group " + line));
                    System.out.println("Detected group: " + currentGroup);
                    continue;
                }

                final Optional<Person> person = PersonGroupReader.fromLine(line);
                person.ifPresentOrElse(System.out::println, () -> System.err.println("Unable to decode line: " + line));

                if (person.isPresent()) {
                     grupy.addNewPerson(currentGroup, person.get());
                }

            }
        }
        System.out.println(grupy);
        grupy.validateContinuity();
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
        if (StringUtils.isBlank(line)) {
            return true;
        }
        return false;
    }
}
