package com.evolve.domain;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountShould {

    private final String accountNumber = "ODUxMDIwNDM5MTAwMDA2NjAyMDAzNTYyNTM=";

    @Test
    void extractBankId() {


//        String encodedString = new String(Base64.getDecoder().decode(accountNumber));
//        System.out.println(encodedString);

        //String accountNumber = "102044050000280200295535";
        assertThat(BankAccount.of( new String(Base64.getDecoder().decode(accountNumber))).extractBankId())
                .hasValue("10204391");

//        assertThat(BankAccount.of("102044050000280200295535").extractBankId())
//                .hasValue("10204391");

    }

}