package com.evolve.gui.person.accounts;

import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.unit.Unit;
import com.evolve.alpaca.unit.services.UnitsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountTooltipService {

    private final UnitsService unitsService;


    Optional<String> forAccountNumber(String accountId) {
        if (StringUtils.isBlank(accountId)) {
            return Optional.empty();
        }

        final Account.AccountType accountType = Account.AccountType.ofAccountingNumber(accountId);
        final String unitNumber = Account.getUnitNumber(accountId);
        final Optional<Unit> maybeUnit = unitsService.getByUnitNumber(unitNumber);

        final String toolTipText;
        if (accountType != null) {
            toolTipText = accountType.getCode() + ": " + accountType + Strings.LINE_SEPARATOR +
                            unitNumber + ": " + maybeUnit.map(Unit::getName).orElse("UNKNOWN");
        } else {
            final String unknownCode = accountId.substring(0, 3);
            toolTipText = "Unknown code: " + unknownCode + " " + Strings.LINE_SEPARATOR +
                    unitNumber + ": " + maybeUnit.map(Unit::getName).orElse("UNKNOWN");
        }

        return Optional.of(toolTipText);
    }

}
