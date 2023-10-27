package com.evolve.services;

import com.evolve.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.filters.Filter;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.dizitart.no2.filters.FluentFilter.where;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountsService {
    private final Nitrite nitrite;

    public List<Account> findByPersonId(String personId) {
        final ObjectRepository<Account> accountRepo = nitrite.getRepository(Account.class);
        return accountRepo.find(where("personId").eq(personId))
                .toList();
    }

    public void insertAccounts(List<Account> accounts) {

        final ObjectRepository<Account> accountsRepo = nitrite.getRepository(Account.class);
        accountsRepo.remove(Filter.ALL);
        accountsRepo.dropAllIndices();
        nitrite.commit();

        accounts
                .stream()
                .filter(account -> StringUtils.isNotBlank(account.getAccountId()))
                .peek(account -> log.info("inserting {}", account))
                .forEach(accountsRepo::insert);
    }
}
