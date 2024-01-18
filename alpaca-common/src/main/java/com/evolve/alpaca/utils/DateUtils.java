package com.evolve.alpaca.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {
    private static final int DATE_MIN_YEAR = 1950;

    public static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Certain dates (joined date, resignation date, removal date) cannot be in 1900-1950 because the organization didn't exist at that time.<br>
     * NOTE: this rule does not apply to the date of birth
     * @return adjusted date to current century
     */
    public static LocalDate adjustDateToCurrentCentury(LocalDate joinedDate) {
        if (joinedDate.getYear() < DATE_MIN_YEAR) {
            return joinedDate.plusYears(100);
        }
        return joinedDate;
    }

}
