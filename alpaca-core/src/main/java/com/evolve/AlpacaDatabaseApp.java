package com.evolve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * This configuration class serves only purpose for running Spring integration tests.
 */
@SpringBootApplication(scanBasePackages = "com.evolve")
@EnableCaching
public class AlpacaDatabaseApp {

    public static void main(String[] args) {
        SpringApplication.run(AlpacaDatabaseApp.class, args);
    }

}
