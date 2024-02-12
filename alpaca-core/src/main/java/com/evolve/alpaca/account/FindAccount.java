package com.evolve.alpaca.account;

import java.util.Collection;
import java.util.List;

public interface FindAccount {

    List<Account> findByPersonId(String personId);

    List<Account> findByUnitAndAccountType(Collection<Account.AccountType> types, Collection<String> unitNumbers);

    List<Account> fetch(AccountLookupCriteria criteria);
}
