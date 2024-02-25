package com.evolve.alpaca.person;

import com.evolve.FindPerson;
import com.evolve.domain.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class PersonController {

    private final FindPerson findPerson;

    @GetMapping(value = "/{id}", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Person getPerson(@PathVariable String id) {
        log.info("getPerson id={}", id);
        var person = findPerson.findById(id);

        log.info("got: {}", person);
        return person;
    }


}
