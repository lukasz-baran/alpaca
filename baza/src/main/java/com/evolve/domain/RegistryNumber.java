package com.evolve.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@ToString
public class RegistryNumber {
    private Integer oldRegistryNum; // numer starej kartoteki
    private Integer registryNum; // numer kartoteki

    public static RegistryNumber onlyOldRegistryNumber(String oldRegistry) {
        return new RegistryNumber(parseOrNull(oldRegistry), null);
    }

    public RegistryNumber(String oldRegistry, String newRegistry) {
        this(parseOrNull(oldRegistry), parseOrNull(newRegistry));
    }

    @JsonIgnore
    public boolean isUseless() {
        return this.oldRegistryNum == null && this.registryNum == null;
    }

    static Integer parseOrNull(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }


}
