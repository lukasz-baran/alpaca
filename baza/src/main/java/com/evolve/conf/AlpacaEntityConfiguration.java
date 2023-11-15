package com.evolve.conf;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.evolve")
@EntityScan("com.evolve")
@Configuration
@EnableAutoConfiguration
public class AlpacaEntityConfiguration {

}
