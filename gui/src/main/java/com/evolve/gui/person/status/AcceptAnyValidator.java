package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.validation.ValidationResult;
import com.evolve.validation.Validator;

public class AcceptAnyValidator implements Validator<PersonStatusChange> {

    @Override
    public ValidationResult validate(PersonStatusChange toValidate) {
        return ValidationResult.empty();
    }
}
