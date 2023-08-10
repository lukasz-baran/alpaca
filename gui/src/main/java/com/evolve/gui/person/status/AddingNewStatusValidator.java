package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.validation.ValidationResult;
import com.evolve.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Validates if new status can be added to the list of existing statuses. <br>
 * (Justification) The same person cannot be born or die twice.
 */
@RequiredArgsConstructor
public class AddingNewStatusValidator implements Validator<PersonStatusChange> {
    private final List<PersonStatusChange> existingStatuses;

    @Override
    public ValidationResult validate(PersonStatusChange newStatus) {
        if (newStatus.getEventType() != PersonStatusChange.EventType.BORN &&
                newStatus.getEventType() != PersonStatusChange.EventType.DIED) {
            return ValidationResult.empty();
        }

        final boolean alreadyExists = existingStatuses.stream()
                .anyMatch(existingStatus -> existingStatus.getEventType() == newStatus.getEventType());

        if (alreadyExists) {
            return new ValidationResult(Set.of("Istnieje już status: " + newStatus.getEventType()));
        }
        return ValidationResult.empty();
    }
}
