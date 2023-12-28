package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BankAccountEntry {

    private String number;

    BankAccount toBankAccount() {
        return BankAccount.of(number);
    }
}
