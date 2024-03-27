package com.evolve.alpaca.importing.importDbf.deducers.status;

import com.evolve.alpaca.importing.DateParser;
import com.evolve.alpaca.importing.PersonStatusDetails;
import com.evolve.alpaca.utils.DateUtils;
import com.evolve.domain.PersonStatusChange;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatusChangesHistoryBuilder {

    public static List<PersonStatusChange> deduceStatusChanges(
            Optional<PersonStatusChange> maybeDob,
            Optional<PersonStatusChange> maybeJoinedDate,
            Optional<PersonStatusDetails> personStatusDetails) {

        final List<PersonStatusChange> statusChanges = new ArrayList<>();

        maybeDob.ifPresent(statusChanges::add);
        maybeJoinedDate.ifPresent(statusChanges::add);

        personStatusDetails.ifPresent(statusDetails -> {
            switch (statusDetails.getStatus()) {
                case DEAD:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.DIED)
                            .when(tryParseDate(statusDetails.getDeathDate()).orElse(null))
                            .originalValue(statusDetails.getDeathDate())
                            .build());
                    break;
                case RESIGNED:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.RESIGNED)
                            .when(tryParseDate(statusDetails.getResignationDate()).orElse(null))
                            .originalValue(statusDetails.getResignationDate())
                            .build());
                    break;
                case REMOVED:
                    statusChanges.add(PersonStatusChange.builder()
                            .eventType(PersonStatusChange.EventType.REMOVED)
                            .when(tryParseDate(statusDetails.getRemovedDate()).orElse(null))
                            .originalValue(statusDetails.getRemovedDate())
                            .build());
                    break;
                case UNKNOWN:
                    break;
            }
        });
        return statusChanges;
    }

    private static Optional<LocalDate> tryParseDate(String date) {
        try {
            return DateParser.parse(date)
                    .map(DateUtils::adjustDateToCurrentCentury);
        } catch (DateTimeException dateTimeException) {
            return Optional.empty();
        }
    }
}
