package com.evolve.alpaca.utils;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

public class TestUtils {

    public static boolean isOnLocalEnv() {
        final Resource resourcePersons = new DefaultResourceLoader().getResource("Z_B_KO.DBF");
        // we don't want this test to fail when there is no DBF files:
        return resourcePersons.exists();
    }
}
