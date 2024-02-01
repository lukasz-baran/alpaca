package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.Person;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthorizedPersonDeducerShould {

    @Mock
    IssuesLogger.ImportIssues importIssues;

    AuthorizedPersonDeducer authorizedPersonDeducer;

    @BeforeEach
    public void setUp() {
        authorizedPersonDeducer = new AuthorizedPersonDeducer(importIssues, true);
    }

    @Test
    void deduceFrom() {
        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("up. mąż Kormoran Jan")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                                .firstName("Kormoran")
                                .lastName("Jan")
                                .relation("mąż")
                                .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("stra Agnieszka Kowalska")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Agnieszka")
                        .lastName("Kowalska")
                        .relation("siostra")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("up. syn Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("syn")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("up. ż. Kos Katarzyna")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Kos")
                        .lastName("Katarzyna")
                        .relation("żona")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("up. m. Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("mąż")
                        .build()));

        assertThat(authorizedPersonDeducer.deduceFrom(Lists.newArrayList("brat Jan Kowalski")))
                .hasValue(List.of(Person.AuthorizedPerson.builder()
                        .firstName("Jan")
                        .lastName("Kowalski")
                        .relation("brat")
                        .build()));
    }

    @Test
    void deduceTwoAuthorizedPersons() {
        final List<String> guesses = Lists.newArrayList("m.Jan Sobieski", "s.Kara Mustafa");

        assertThat(authorizedPersonDeducer.deduceFrom(guesses).orElseThrow())
            .containsExactlyInAnyOrder(
                    Person.AuthorizedPerson.builder().firstName("Jan").lastName("Sobieski").relation("mąż").build(),
                    Person.AuthorizedPerson.builder().firstName("Kara").lastName("Mustafa").relation("syn").build()
            );

        assertThat(authorizedPersonDeducer.removeGuesses(guesses)).isEmpty();
    }

    @Test
    void deduceNonstandardNotation() {
        // given
        final List<String> guesses = Lists.newArrayList("up. Zbigniew Skrzypczak mąż");

        // when & then
        assertThat(authorizedPersonDeducer.deduceFrom(guesses).orElseThrow())
                .containsExactly(Person.AuthorizedPerson.builder().firstName("Zbigniew").lastName("Skrzypczak").relation("mąż").build());

        assertThat(guesses)
                .isEmpty();
        assertThat(authorizedPersonDeducer.removeGuesses(guesses)).isEmpty();
    }
}