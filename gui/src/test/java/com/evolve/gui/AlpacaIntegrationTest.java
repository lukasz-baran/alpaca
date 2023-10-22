package com.evolve.gui;

import com.evolve.FindPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@ExtendWith(Spring)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(SpringExtension.class)
public class AlpacaIntegrationTest {

    @Autowired
    FindPerson findPerson;

    @Test
    void testApp() {
        findPerson.findByUnitId("abcd");
    }

}
