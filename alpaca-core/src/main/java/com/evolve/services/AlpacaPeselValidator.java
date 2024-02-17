package com.evolve.services;

import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.evolve.domain.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.unak7.peselvalidator.GenderEnum;
import pl.unak7.peselvalidator.PeselValidator;
import pl.unak7.peselvalidator.PeselValidatorImpl;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AlpacaPeselValidator implements Validator<String> {
    private final LocalDate dob;
    private final Person.Gender gender;

    @Override
    public ValidationResult validate(String pesel) {
        final PeselValidator peselValidator = new PeselValidatorImpl();
        log.info("validating pesel together with dob {} and gender {}", dob, gender);

        final GenderEnum genderEnum = toGenderEnum(gender);
        final Optional<LocalDate> maybeDob = Optional.ofNullable(dob);

        if (maybeDob.isPresent() && !peselValidator.validate(pesel, maybeDob.get(), genderEnum)) {
            return invalid(pesel);
        }
        if (!peselValidator.validate(pesel, genderEnum)) {
            return invalid(pesel);
        }
        return ValidationResult.empty();
    }

    private ValidationResult invalid(String pesel) {
        return ValidationResult.of("Podany numer PESEL: " + pesel + " jest niepoprawny");
    }

    GenderEnum toGenderEnum(Person.Gender personGender) {
        return Optional.ofNullable(personGender)
                .map(gender -> switch (gender) {
                    case MALE -> GenderEnum.MALE;
                    case FEMALE -> GenderEnum.FEMALE;
                })
                .orElse(GenderEnum.OTHER);
    }

}
