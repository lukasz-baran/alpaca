package com.evolve.alpaca.conf;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

@Component
@Slf4j
public class LocalUserConfiguration implements InitializingBean {

    public static final String Z_B_KO_DBF_LOCATION = "Z_B_KO_DBF_LOCATION";
    public static final String PLAN_DBF_LOCATION = "PLAN_DBF_LOCATION";

    public static final String PROPERTIES_FILE = System.getProperty("user.home") + File.separator + ".alpaca" +
            File.separator + "alpaca.properties";

    @SneakyThrows
    public void setConfigurationProperty(String key, String value) {
        final Properties properties = getOrCreate();
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(PROPERTIES_FILE), null);
    }

    public Optional<String> loadProperty(String key) {
        final Properties properties = getOrCreate();
        return Optional.ofNullable(properties.getProperty(key));
    }

    @SneakyThrows
    private Properties getOrCreate() {
        final Properties properties = new Properties();
        if (Files.exists(Path.of(PROPERTIES_FILE), LinkOption.NOFOLLOW_LINKS)) {
            properties.load(new FileInputStream(PROPERTIES_FILE));
        }
        return properties;
    }

    @Override
    public void afterPropertiesSet() {
        final File file = new File(PROPERTIES_FILE);
        if (!file.exists()) {
            try {
                if (!file.getParentFile().mkdirs()) {
                    log.warn("Unable to create the configuration directory");
                    return;
                }
                if (file.createNewFile()) {
                    log.info("File was created");
                } else {
                    log.warn("Unable to create the configuration file");
                }
            } catch (IOException e) {
                log.error("Unable to create the configuration file");
            }
        }
    }


}
