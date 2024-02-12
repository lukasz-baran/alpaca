package com.evolve.alpaca.gui.accounts;

import com.evolve.alpaca.account.Account;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AccountEntry {
    private String accountId;
    private Account.AccountType accountType;
    private String unitNumber;
    private String accountName;
    private String personId;

    public static AccountEntry of(Account account) {
        return new AccountEntry(account.getAccountId(), account.getAccountType(),
                account.getUnitNumber(), account.getAccountName(), account.getPersonId());
    }

    public boolean matches(String filteredText) {
        if (filteredText == null || filteredText.isEmpty()) {
            return true;
        }

        final String lowerCaseFilter = filteredText.toLowerCase();
        if (trimToEmpty(accountId).toLowerCase().contains(lowerCaseFilter)) {
            return true;
        }

        return trimToEmpty(accountName).toLowerCase().contains(lowerCaseFilter);
    }
}