package com.evolve.content;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ContentServicesConfiguration {

    @Bean
    ContentStoreService contentStoreService(FileRepository fileRepository, FileContentStore fileContentStore) {
        return new ContentStoreService(fileRepository, fileContentStore, Clock.systemDefaultZone());
    }
}
