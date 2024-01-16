package com.evolve.alpaca.importing.importDbf;

import com.evolve.domain.Person;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PostImportStepService {
    private final PersonRepository personRepository;

    public void process() {
        final List<Person> persons = personRepository.findAll();

        persons.forEach(person -> {

        });

    }
}
