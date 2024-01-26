package com.evolve.domain;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embeddable;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@ToString
@Embeddable
public class RegistryNumber {
    private Integer registryNum;

    public static RegistryNumber of(String input) {
        if (StringUtils.isBlank(input)) {
            return new RegistryNumber(null);
        }

        final String sanitizedInput = sanitize(input);
        return new RegistryNumber(parseOrNull(sanitizedInput));
    }

    private static String sanitize(@NonNull String input) {
        if (input.endsWith(".")) {
            return input.replaceAll("\\.", "");
        }
        return input;
    }

    public static RegistryNumber fromText(String newRegistry) {
        return of(parseOrNull(newRegistry));
    }

    public static RegistryNumber of(Integer integer) {
        return new RegistryNumber(integer);
    }

    public Optional<Integer> getNumber() {
        return Optional.ofNullable(registryNum);
    }

    static Integer parseOrNull(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }


}
