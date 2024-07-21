package com.evolve.domain;

import com.evolve.alpaca.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.evolve.domain.PersonAssertion.assertPerson;

public class PersonDeserializeShould {
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;

    @Test
    void deserializeJson() throws IOException {
        final String json = Resources.toString(Resources.getResource("json/personSerialized.json"), Charset.defaultCharset());

        final Person person = objectMapper.readValue(json, Person.class);

        assertPerson(person)
                .hasFirstName("Janina")
                .hasLastName("Kowalska");
        // TODO should assert other fields
    }
}
