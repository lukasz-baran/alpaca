package com.evolve.alpaca.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtils {

    public static String getFileExt(String filename) {
        if (filename.contains(".")) {
            Pattern pattern = Pattern.compile("\\.([^.\\\\/]+$)");
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return "";
    }
}
