package com.evolve.alpaca.importing.importDbf.account;

import com.evolve.alpaca.account.Account;
import org.junit.jupiter.api.Test;

import static com.evolve.alpaca.account.AccountAssertion.assertAccount;

class AccountsFactoryShould {

    @Test
    void decodePersonId() {
        final DbfAccount dbfAccount = DbfAccount.builder()
                .KS("2032310063")
                .NA("Jan Kowalski")
                .build();

        final Account result = AccountsFactory.from(dbfAccount);

        assertAccount(result)
                .hasPersonId("10063")
                .hasAccountId("2032310063")
                .hasAccountName("Jan Kowalski")
                .hasUnitNumber("23")
                .hasAccountType(Account.AccountType.PAYDAY_LOANS);
    }

}