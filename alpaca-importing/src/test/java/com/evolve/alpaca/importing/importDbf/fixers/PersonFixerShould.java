package com.evolve.alpaca.importing.importDbf.fixers;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.PersonStatusChange;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.time.LocalDate;
import java.util.List;

import static com.evolve.alpaca.importing.importDbf.fixers.PersonFixer.JOINED_DATE_KEY;
import static com.evolve.domain.PersonAssertion.assertPerson;
import static org.assertj.core.api.Assertions.assertThat;

class PersonFixerShould {

    final ResourceLoader resourceLoader = new DefaultResourceLoader();
    final Resource resource = resourceLoader.getResource("fixer.csv");
    final PersonFixer personFixer = new PersonFixer(resource);

    @Test
    void loadFile() {
        // when
        int result = personFixer.loadData();

        // then
        assertThat(result)
                .isGreaterThan(0);
    }

    @Test
    void handleJoinedDate() {
        // given
        final LocalDate born = LocalDate.of(1944, 9, 23);
        final LocalDate resigned = LocalDate.of(2020,6, 29);
        final LocalDate joined = LocalDate.of(1993, 2, 14);

        final List<PersonStatusChange> changes = Lists.newArrayList(PersonStatusChange.born(born),
                PersonStatusChange.resigned(resigned));

        final Person person = Person.builder()
                .dob(born)
                .statusChanges(changes)
                .build();

        // when
        personFixer.fix(person, JOINED_DATE_KEY, "14.02.93");

        // then
        assertPerson(person)
                .wasBornOn(born)
                .hasStatusHistory(PersonStatusChange.born(born),
                        PersonStatusChange.joined(joined),
                        PersonStatusChange.resigned(resigned))
                .hasStatus(PersonStatus.RESIGNED);

    }

}