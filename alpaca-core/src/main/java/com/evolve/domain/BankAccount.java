package com.evolve.domain;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.IBANValidator;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class BankAccount implements Serializable {
    public static final IBANValidator IBAN_VALIDATOR = new IBANValidator(
            new IBANValidator.Validator[]{IBANValidator.getInstance().getValidator("PL")});

    private String number;
    private String notes;

    public static BankAccount of(String number) {
        return of(number, null);
    }

    public static BankAccount of(String number, String notes) {
        return new BankAccount(number, notes);
    }

    public static boolean isValid(String input) {
        return IBAN_VALIDATOR.isValid("PL" + input);
    }

    public Optional<String> extractBankId() {
        if (isValid(number)) {
            return Optional.of(StringUtils.substring(number, 2, 10));
        }
        return Optional.empty();
    }

}
