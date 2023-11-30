package com.evolve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This configuration class serves only purpose for running Spring integration tests.
 */
@SpringBootApplication(scanBasePackages = "com.evolve")
public class AlpacaRestServicesApp {

    public static void main(String[] args) {
        SpringApplication.run(AlpacaRestServicesApp.class, args);
    }

}
