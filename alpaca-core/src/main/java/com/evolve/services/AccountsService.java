package com.evolve.services;

import com.evolve.domain.Account;
import com.evolve.repo.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountsService {
    private final AccountRepository accountRepository;

    public List<Account> findByPersonId(String personId) {
        return accountRepository.findByPersonId(personId);
    }

    // TODO Do we need extra method?
    public List<Account> findByUnitAndAccountType(Collection<Account.AccountType> types,
                                                  Collection<String> unitNumbers) {
        return accountRepository.findByAccountTypeInOrUnitNumberIn(types, unitNumbers);
    }

    public void insertAccounts(List<Account> accounts) {
        accountRepository.deleteAll();
        accounts
                .stream()
                .filter(account -> StringUtils.isNotBlank(account.getAccountId()))
                .peek(account -> log.info("inserting {}", account))
                .forEach(accountRepository::save);
    }
}
