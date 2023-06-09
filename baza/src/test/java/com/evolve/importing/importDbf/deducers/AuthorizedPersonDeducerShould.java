package com.evolve.importing.importDbf.deducers;

import com.evolve.domain.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthorizedPersonDeducerShould {

    @Mock
    IssuesLogger.ImportIssues importIssues;

    @InjectMocks
    AuthorizedPersonDeducer authorizedPersonDeducer;

    @Test
    void deduceFrom() {
        assertThat(authorizedPersonDeducer.deduceFrom(List.of("up. mąż Kormoran Jan")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                                .firstName("Kormoran")
                                .lastName("Jan")
                                .relation("mąż")
                                .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(List.of("stra Agnieszka Kowalska")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Agnieszka")
                        .lastName("Kowalska")
                        .relation("siostra")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(List.of("up. syn Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("syn")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(List.of("up. ż. Kos Katarzyna")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Kos")
                        .lastName("Katarzyna")
                        .relation("żona")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(List.of("up. m. Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("mąż")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(List.of("brat Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("brat")
                        .build()));

    }
}