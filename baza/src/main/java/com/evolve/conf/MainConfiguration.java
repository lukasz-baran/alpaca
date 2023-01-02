package com.evolve.conf;

import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mapper.JacksonMapperModule;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class MainConfiguration {
    @Value("${nitrite.database.path}")
    private String databaseFilePath;

    @Value("${nitrite.database.username}")
    private String databaseFileUsername;

    @Value("${nitrite.database.password}")
    private String databaseFilePassword;

    @Bean
    Nitrite nitrateDb() {
        final MVStoreModule storeModule = MVStoreModule.withConfig()
                .filePath(databaseFilePath)
                .compress(true)
                .build();
        log.info("database initialized");
        return Nitrite.builder()
                .loadModule(storeModule)
                .loadModule(new JacksonMapperModule())
                .openOrCreate(databaseFileUsername, databaseFilePassword);
    }
}
