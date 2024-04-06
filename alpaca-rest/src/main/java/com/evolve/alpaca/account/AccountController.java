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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Account> getAllAccounts() {
        log.info("get all accounts");
        return findAccount.fetch(AccountLookupCriteria.ALL);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<Account> getPersonAccounts(@PathVariable String id) {
        log.info("get account by person id={}", id);
        final List<Account> accounts = findAccount.findByPersonId(id);

        log.info("got: {}", accounts);
        return accounts;
    }
}
