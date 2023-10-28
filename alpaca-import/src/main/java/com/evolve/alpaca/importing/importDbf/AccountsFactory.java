package com.evolve.alpaca.importing.importDbf;

import com.evolve.alpaca.importing.importDbf.domain.DbfAccount;
import com.evolve.domain.Account;
import org.apache.commons.lang.StringUtils;

public class AccountsFactory {

    private static final int REAL_PERSON_ACCOUNT_LENGTH = 10;

    public static Account from(DbfAccount dbfAccount) {
        return Account.builder()
                .accountId(dbfAccount.getKS())
                .accountName(getAccountName(dbfAccount))
                .personId(getPersonId(dbfAccount))
                .unitNumber(getUnitNumber(dbfAccount))
                .accountType(getAccountType(dbfAccount))
                .build();
    }

    static String getPersonId(DbfAccount dbfAccount) {
        if (dbfAccount.getKS().length() != REAL_PERSON_ACCOUNT_LENGTH) {
            return null;
        }
        return StringUtils.substring(dbfAccount.getKS(), 5, 10);
    }

    static String getUnitNumber(DbfAccount dbfAccount) {
        if (dbfAccount.getKS().length() != REAL_PERSON_ACCOUNT_LENGTH) {
            return null;
        }
        return StringUtils.substring(dbfAccount.getKS(), 3, 5);
    }

    static Account.AccountType getAccountType(DbfAccount dbfAccount) {
        if (dbfAccount.getKS().length() != REAL_PERSON_ACCOUNT_LENGTH) {
            return null;
        }
        return Account.AccountType.ofAccountingNumber(dbfAccount.getKS());
    }

    static String getAccountName(DbfAccount dbfAccount) {
        if (StringUtils.isNotBlank(dbfAccount.getNA1())) {
            return dbfAccount.getNA() + dbfAccount.getNA1();
        }
        return dbfAccount.getNA();
    }

}
