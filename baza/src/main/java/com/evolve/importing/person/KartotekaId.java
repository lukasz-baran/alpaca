package com.evolve.importing.person;

import com.evolve.importing.exception.ImportFailedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KartotekaId {
    private final String id;
    private final boolean missing; // TODO replace with getter based on Optional

    public static KartotekaId of(String input) {
        if (StringUtils.isBlank(input)) {
            return new KartotekaId(null, true);
        }

        final String sanitizedInput = sanitize(input);
        try {
            Integer.parseInt(sanitizedInput);
        } catch (NumberFormatException nfe) {
            throw new ImportFailedException("Cannot parse " + input, nfe);
        }

        return new KartotekaId(sanitizedInput, false);
    }

    private static String sanitize(@NonNull String input) {
        if (input.endsWith(".")) {
            return input.replaceAll("\\.", "");
        }
        return input;
    }

    @Override
    public String toString() {
        if (this.missing) {
            return "(brak)";
        }
        return this.id;
    }
}
