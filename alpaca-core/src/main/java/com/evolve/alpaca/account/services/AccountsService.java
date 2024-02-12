package com.evolve.alpaca.account.services;

import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.account.AccountLookupCriteria;
import com.evolve.alpaca.account.FindAccount;
import com.evolve.alpaca.account.repo.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountsService implements FindAccount {
    private final AccountRepository accountRepository;

    @Override
    public List<Account> findByPersonId(String personId) {
        return accountRepository.findByPersonId(personId);
    }

    @Override
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

    @Override
    public List<Account> fetch(AccountLookupCriteria criteria) {
        return accountRepository.findAll(criteria.getSort());
    }
}
