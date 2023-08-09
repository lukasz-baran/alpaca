package com.evolve.alpaca.conf;

import javafx.beans.property.SimpleObjectProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * TODO: The values in this class should be somehow persisted to db.
 */
@Configuration
public class AlpacaRuntimeConfiguration {

    @Bean
    public SimpleObjectProperty<File> lastKnownDirectoryProperty() {
        return new SimpleObjectProperty<>();
    }

}
