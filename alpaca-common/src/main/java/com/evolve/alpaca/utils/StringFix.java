package com.evolve.alpaca.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StringFix {

    public static String capitalizeLastName(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        if (input.contains("-")) {
            return Arrays.stream(input.split("-")).map(StringFix::capitalize).collect(Collectors.joining("-"));
        }
        return capitalize(input);
    }

    public static String capitalize(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        return StringUtils.capitalize(input.toLowerCase());
    }

}
