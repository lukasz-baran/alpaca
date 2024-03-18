package com.evolve.alpaca.conf;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;

@Configuration
@PropertySource(value = {"classpath:alpaca-common.yml"}, factory = YamlPropertyLoaderFactory.class)
@Getter
@Slf4j
public class AlpacaCommonConfiguration implements InitializingBean {

    private final String applicationVersion;
    private final String gitCommitNumber;

    @Autowired
    AlpacaCommonConfiguration(@Value("${application.version}") String applicationVersion,
                                     @Value("${git.commit.number}") String gitCommitNumber) {
        this.applicationVersion = applicationVersion;
        this.gitCommitNumber = gitCommitNumber;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Alpaca version: {} build: {}", applicationVersion, gitCommitNumber);
    }

    public String getFullVersionNumber() {
        return applicationVersion + " hash: " + gitCommitNumber;
    }
}
