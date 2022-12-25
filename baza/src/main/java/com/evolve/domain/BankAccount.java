package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BankAccount {

    private final String number;
    private final String bank;
    // maybe some other data will be needed

}
