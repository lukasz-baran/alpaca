package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;

public class AcceptAnyValidator implements Validator<PersonStatusChange> {

    @Override
    public ValidationResult validate(PersonStatusChange toValidate) {
        return ValidationResult.empty();
    }
}
