package com.evolve.gui.person.bankAccounts;

import com.evolve.external.BankDetails;
import com.evolve.external.ValidateNbpNumbersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountTooltipShould {
    @Mock ValidateNbpNumbersService validateNbpNumbersService;
    @Mock BankDetails bankDetails;
    @InjectMocks
    BankAccountTooltip bankAccountTooltip;

    @Test
    void generateTooltipForInvalidBankAccountNumber() {
        final String tooltip = bankAccountTooltip.buildTooltipText("123213");

        assertThat(tooltip)
                .isEqualTo("Numer konta 123213 jest niepoprawny");
    }

    @Test
    void showTooltipWhenExternalServiceResponds() {
        final String validAccountNumber = "83101010230000261395100000";
        when(validateNbpNumbersService.getBankDetails(anyString()))
                .thenReturn(Optional.of(bankDetails));

        final String result = bankAccountTooltip.buildTooltipText(validAccountNumber);

        assertThat(result)
                .isEqualTo(validAccountNumber + " - brak danych");
    }

}