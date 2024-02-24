package com.evolve;

import de.cronn.testutils.h2.H2Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:test.properties")
@Import(H2Util.class)
public abstract class AlpacaAbstractIntegrationTest {

    @BeforeEach
    void resetDatabase(@Autowired H2Util h2Util) {
        h2Util.resetDatabase();
    }

}
