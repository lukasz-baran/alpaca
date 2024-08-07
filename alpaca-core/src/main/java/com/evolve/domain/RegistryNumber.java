package com.evolve.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@ToString
@Embeddable
public class RegistryNumber implements Serializable {
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

    @JsonIgnore
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
