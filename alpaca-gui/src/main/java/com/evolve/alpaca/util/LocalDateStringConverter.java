package com.evolve.alpaca.util;

import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class LocalDateStringConverter extends StringConverter<LocalDate> {
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private boolean hasParseError = false;

    public boolean hasParseError(){
        return hasParseError;
    }

    public static String localDateToString(LocalDate localDate) {
        log.debug("toString called {}", localDate);
        if (localDate == null) {
            return "";
        }
        return DATE_FORMATTER.format(localDate);
    }

    @Override
    public String toString(LocalDate localDate) {
        return localDateToString(localDate);
    }

    @Override
    public LocalDate fromString(String formattedString) {
        log.debug("fromString called {}", formattedString);
        if (StringUtils.isBlank(formattedString)) {
            hasParseError = false;
            return null;
        }

        try {
            LocalDate date = LocalDate.from(DATE_FORMATTER.parse(formattedString));
            hasParseError = false;
            return date;
        } catch (DateTimeParseException parseExc){
            hasParseError = true;
            return null;
        }
    }

}
