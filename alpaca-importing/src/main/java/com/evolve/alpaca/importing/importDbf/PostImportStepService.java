package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusDetails;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Purpose of this service is to update status of people based on
 * <ul>
 *     <li>original data coming from DBF files</li>
 *     <li></li>
 * </ul>
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class PostImportStepService {
    private final PersonRepository personRepository;

    public void processAll() {
        final List<Person> persons = personRepository.findAll()
                .stream()
                .filter(person -> Objects.isNull(person.getStatus()))
                //.filter(person -> "11022".equals(person.getPersonId()))
                .toList();
        process(persons);
    }

    public void process(List<Person> persons ) {
        log.info("Number of persons to be verified: {}", persons.size());

        persons.forEach(person -> {
                var result = PersonStatusDetails.basedOnStatusChange(person.getStatusChanges());
                log.info("history of statuses: {} deduced: {}",
                        LogUtil.printJson(person.getStatusChanges()),
                        result
                        );
        });

    }
}
