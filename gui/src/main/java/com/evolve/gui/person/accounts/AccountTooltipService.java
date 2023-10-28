package com.evolve.gui.person.accounts;

import com.evolve.domain.Account;
import com.evolve.services.UnitsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class AccountTooltipService {

    private final UnitsService unitsService;


    String forAccountNumber(String accountId) {
        if (StringUtils.isBlank(accountId)) {
            return StringUtils.EMPTY;
        }

        final Account.AccountType accountType = Account.AccountType.ofAccountingNumber(accountId);

        final String toolTipText =
                accountType.getCode() + ": " + accountType;



        return toolTipText;
    }

}
