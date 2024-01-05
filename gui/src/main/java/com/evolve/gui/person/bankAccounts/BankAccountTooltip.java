package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import com.evolve.external.BankDetails;
import com.evolve.external.ValidateNbpNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BankAccountTooltip {

    private final ValidateNbpNumbersService validateNbpNumbersService;

    String buildTooltipText(String accountNumber) {
        if (!BankAccount.isValid(accountNumber)) {
            return accountNumber + " jest niepoprawny";
        }

        final BankAccount bankAccount = BankAccount.of(accountNumber);
        final Optional<BankDetails> bankDetails = bankAccount.extractBankId()
                .flatMap(validateNbpNumbersService::getBankDetails);
        return bankDetails.flatMap(this::fromDetails)
                .orElse(accountNumber + " - brak danych");
    }

    private Optional<String> fromDetails(BankDetails bankDetails) {
        Optional<BankDetails.Owner> owner = bankDetails.getListaWlascicieli().stream().findFirst();
        return owner.map(o -> o.symbol() + " (" + o.nazwa() + ")");
    }


}
