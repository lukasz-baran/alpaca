package com.evolve.alpaca.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/account", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class AccountController {
    private final FindAccount findAccount;

    @GetMapping(value = "/{id}", produces="application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Account> getPersonAccounts(@PathVariable String id) {
        log.info("getPerson id={}", id);
        final List<Account> accounts = findAccount.findByPersonId(id);

        log.info("got: {}", accounts);
        return accounts;
    }
}
