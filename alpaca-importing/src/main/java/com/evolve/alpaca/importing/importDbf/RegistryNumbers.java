package com.evolve.alpaca.importing.importDbf;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class RegistryNumbers {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Set<Integer> allNumbers = new HashSet<>();
    private final Set<Integer> oldNumbers = new HashSet<>();

    static RegistryNumbers.RegistryNumbersLoader usingLoader(Resource resource) {
        return new RegistryNumbersLoader(resource, new RegistryNumbers());
    }


    public Numbers parseLine(String line) {
        final Numbers numbers = getRegistryNumber(line);
        if (!numbers.isEmpty()) {
            numbers.getNumber().ifPresent(newNumber -> {
                if (allNumbers.contains(newNumber)) {
                    log.debug("New registry number already found: {}", newNumber);
                }
                allNumbers.add(newNumber);
            });

            numbers.getOldNumber().ifPresent(oldNumber -> {
                if (oldNumbers.contains(oldNumber)) {
                    log.debug("Old registry number already found: {}", oldNumber);
                }
                oldNumbers.add(oldNumber);
            });
        }
        return numbers;
    }

    Numbers getRegistryNumber(String number) {
        if (StringUtils.isBlank(number) || StringUtils.containsOnly(number, ".")) {
            return Numbers.empty();
        }

        if (number.startsWith("NKP") || number.equals("813-03-34-838")) { // strange records which we don't understand
            return Numbers.empty();
        }

        if (number.length() == 12 || number.length() == 11 || number.length() == 13 || number.length() == 10) {
            return Numbers.ofTwelve(number);
        }

        if (number.length() == 6 || number.length() == 5 || number.length() == 4 || number.length() == 7) {
            return Numbers.single(number);
        }

        if (number.length() == 9) {
            return Numbers.single(number);
        }

        throw new AssertionError("failed: " + number.length());
    }

    public record Numbers(Integer number, Integer oldNumber) {

        public static Numbers empty() {
            return new Numbers(null, null);
        }

        public static Numbers ofTwelve(String number) {
            final String firstPart = number.substring(0, 6);
            final String lastPart = number.substring(6);
            return new Numbers(skipDots(lastPart), skipDots(firstPart));
        }

        public static Numbers single(String number) {
            return new Numbers(Integer.parseInt(StringUtils.deleteWhitespace(number)), null);
        }

        private static Integer skipDots(String number) {
            if (StringUtils.isBlank(number) || number.contains(".")) {
                return null;
            }
            return Integer.parseInt(StringUtils.deleteWhitespace(number));
        }

        public Optional<Integer> getNumber() { return Optional.ofNullable(number); }
        public Optional<Integer> getOldNumber() { return Optional.ofNullable(oldNumber); }
        public boolean isEmpty() { return getNumber().isEmpty() && getOldNumber().isEmpty(); }
    }


    @RequiredArgsConstructor
    public static class RegistryNumbersLoader {
        private final Resource resource;
        private final RegistryNumbers registryNumbers;

        @SneakyThrows
        public void loadNumbers() {
            if (!resource.exists()) {
                return;
            }
            final InputStream csvData = resource.getInputStream();

            final CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setDelimiter(',')
                    .setIgnoreEmptyLines(true)
                    .setHeader("number")
                    .setSkipHeaderRecord(true)
                    .build();


            try (CSVParser parser = CSVParser.parse(csvData, DEFAULT_CHARSET, format)) {
                final List<CSVRecord> csvRecords = parser.getRecords();
                csvRecords.forEach(csvRecord -> {
                    final String id = csvRecord.get("number");
                    registryNumbers.parseLine(id);
                });
            }
        }

    }
}
