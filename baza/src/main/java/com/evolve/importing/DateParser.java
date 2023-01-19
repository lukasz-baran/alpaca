package com.evolve.importing;

import java.time.LocalDate;
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

    public static final Pattern DOB = Pattern.compile(DAY_PATTERN + DATE_SEPARATOR + MONTH_PATTERN + DATE_SEPARATOR + YEAR_PATTERN);

    public static Optional<LocalDate> parse(String input) {
        final Matcher matcher = DOB.matcher(input);

        if (matcher.matches()) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = sanitizeYear(matcher.group(3));

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
