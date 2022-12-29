package com.evolve.utils;

import org.apache.commons.lang3.StringUtils;

public class StringFix {

    public static String capitalize(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        return StringUtils.capitalize(input.toLowerCase());
    }

}
