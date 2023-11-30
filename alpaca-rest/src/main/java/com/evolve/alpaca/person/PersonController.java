package com.evolve.alpaca.person;

import com.evolve.domain.Person;
import com.evolve.services.PersonsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class PersonController {

    private final PersonsService personsService;

    @GetMapping(value = "/{id}", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Person getPerson(@PathVariable String id) {
        log.info("getPerson id={}", id);
        var person =  personsService.findById(id);

        log.info("got: {}", person);
        return person;
    }


}
