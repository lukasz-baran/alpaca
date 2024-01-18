package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusDetails;
import com.evolve.repo.jpa.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Purpose of this service is to update status of people based on
 * <ul>
 *     <li>original data coming from DBF files</li>
 *     <li>data from fixer</li>
 *     <li>TBD: accounts data</li>
 * </ul>
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class PostImportStepService {
    private static final List<String> PERSON_IDS = List.of("01010",
            "22074");
    private final PersonRepository personRepository;

    public void processAll() {
        final List<Person> persons = personRepository.findAll()
                .stream()
                //.filter(person -> Objects.isNull(person.getStatus()))
                //.filter(person -> PERSON_IDS.contains(person.getPersonId()))
                .toList();
        process(persons);
    }

    public void process(List<Person> persons ) {
        log.info("Number of persons to be verified: {}", persons.size());

        persons.forEach(person -> {
                PersonStatusDetails result = PersonStatusDetails.basedOnStatusChange(person.getStatusChanges());
                log.info("id {} history of statuses: {} deduced: {}",
                        person.getPersonId(),
                        LogUtil.printJson(person.getStatusChanges()),
                        result
                        );

                if (!result.equals(person.getStatus())) {
                    log.info("DIFFERENT STATUS deduced: {} actual: {}", result, person.getStatus());

                    person.setStatus(result);
                    personRepository.save(person);
                }

        });

    }
}
