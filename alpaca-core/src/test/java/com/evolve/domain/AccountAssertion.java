package com.evolve.domain;

import com.evolve.alpaca.account.Account;
import org.assertj.core.api.ObjectAssert;

import static org.assertj.core.api.Assertions.assertThat;


public class AccountAssertion extends ObjectAssert<Account> {

    AccountAssertion(Account account) {
        super(account);
    }

    public static AccountAssertion assertAccount(Account account) {
        return new AccountAssertion(account);
    }

    public AccountAssertion hasAccountId(String accountId) {
        assertThat(actual.getAccountId()).isEqualTo(accountId);
        return this;
    }

    public AccountAssertion hasPersonId(String personId) {
        assertThat(actual.getPersonId()).isEqualTo(personId);
        return this;
    }

    public AccountAssertion hasAccountName(String accountName) {
        assertThat(actual.getAccountName()).isEqualTo(accountName);
        return this;
    }

    public AccountAssertion hasAccountType(Account.AccountType accountType) {
        assertThat(actual.getAccountType()).isEqualTo(accountType);
        return this;
    }

    public AccountAssertion hasUnitNumber(String unitNumber) {
        assertThat(actual.getUnitNumber()).isEqualTo(unitNumber);
        return this;
    }

}
