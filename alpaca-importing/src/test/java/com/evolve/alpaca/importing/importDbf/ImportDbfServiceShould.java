package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.ddd.CommandCollector;
import com.evolve.alpaca.ddd.CommandsApplier;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.domain.Person;
import com.evolve.services.AccountsService;
import com.evolve.services.PersonEditService;
import com.evolve.services.PersonsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class ImportDbfServiceShould {

    @Mock ApplicationEventPublisher applicationEventPublisher;
    @Mock AccountsService accountsService;
    @Mock PersonFixer personFixer;
    @Mock PersonsService personsService;

    @Mock CommandsApplier commandsApplier;
    @Mock CommandCollector commandCollector;
    @Mock PersonEditService personEditService;

    ImportDbfService importDbfService;

    @BeforeEach
    public void setUp() {
        when(personFixer.fixData(any(Person.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        importDbfService = new ImportDbfService(applicationEventPublisher, personsService, personFixer, accountsService,
                commandsApplier, commandCollector, personEditService);
    }

    @Test
    @Disabled
    void importDbfData() throws IOException {
        final Resource resource = new DefaultResourceLoader().getResource("Z_B_KO.DBF");

        final List<Person> persons = importDbfService.startImport(resource.getFile().getPath(), null);

        assertThat(persons.size()).isGreaterThan(0);
    }

}