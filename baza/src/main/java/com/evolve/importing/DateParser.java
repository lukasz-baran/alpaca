package com.evolve.importing;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Capable of parsing following ill-formatted dates: <pre>30,11.67</pre>
 */
public class DateParser {

    static final String DATE_SEPARATOR = "[,-.]";
    static final String DAY_PATTERN = "(\\d{1,2})";
    static final String MONTH_PATTERN = "(\\d{1,2})";
    static final String YEAR_PATTERN = "(\\d{2,4})";

    public static final Pattern DATE_PATTERN = Pattern.compile(DAY_PATTERN + DATE_SEPARATOR + MONTH_PATTERN + DATE_SEPARATOR + YEAR_PATTERN);

    static final String ROMAN_MONTH_PATTERN = "(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII)";
    static final Pattern DATE_WITH_ROMAN_LITERALS_PATTERN = Pattern.compile(DAY_PATTERN + DATE_SEPARATOR + ROMAN_MONTH_PATTERN + DATE_SEPARATOR + YEAR_PATTERN);

    static final Map<String,Integer> ROMAN_LITERALS_TO_NUMBERS = ImmutableMap.<String,Integer>builder()
            .put("I", 1)
            .put("II", 2)
            .put("III", 3)
            .put("IV", 4)
            .put("V", 5)
            .put("VI", 6)
            .put("VII", 7)
            .put("VIII", 8)
            .put("IX", 9)
            .put("X", 10)
            .put("XI", 11)
            .put("XII", 12)
            .build();

    public static Optional<LocalDate> parse(String input) {
        if (input == null) {
            return Optional.empty();
        }

        final Matcher matcher = DATE_PATTERN.matcher(input);
        if (matcher.matches()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = sanitizeYear(matcher.group(3));
            return Optional.of(LocalDate.of(year, month, day));
        }

        final Matcher romanMatcher = DATE_WITH_ROMAN_LITERALS_PATTERN.matcher(input);
        if (romanMatcher.matches()) {
            int day = Integer.parseInt(romanMatcher.group(1));
            int month = ROMAN_LITERALS_TO_NUMBERS.get(romanMatcher.group(2));
            int year = sanitizeYear(romanMatcher.group(3));
            return Optional.of(LocalDate.of(year, month, day));
        }

        return Optional.empty();
    }

    static int sanitizeYear(String year) {
        if (year.length() == 2) {
            return Integer.parseInt("19" + year);
        }
        return Integer.parseInt(year);
    }

}
