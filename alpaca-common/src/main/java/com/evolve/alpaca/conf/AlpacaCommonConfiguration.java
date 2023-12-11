package com.evolve.alpaca.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AlpacaCommonConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
