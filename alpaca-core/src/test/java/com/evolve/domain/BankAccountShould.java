package com.evolve.domain;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountShould {

    private static final String ACCOUNT_NUMBER = "ODUxMDIwNDM5MTAwMDA2NjAyMDAzNTYyNTM=";

    @Test
    void extractBankId() {
        final String fullBankAccountNumber = new String(Base64.getDecoder().decode(ACCOUNT_NUMBER));
        final String withoutNotes = null;

        assertThat(BankAccount.of(fullBankAccountNumber, withoutNotes).extractBankId())
                .hasValue("10204391");
    }

}