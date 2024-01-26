package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Unit represents: medical institution or other type of assignments
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
public class Unit implements Serializable {
    private static final String SEPARATOR = "–";

    public static final String EXEMPT_FROM_FEES_UNIT_NUMBER = "95";
    public static final String RESIGNED_UNIT_NUMBER = "97";
    public static final String REMOVED_UNIT_NUMBER = "98";
    public static final String DECEASED_UNIT_NUMBER = "99";

    public static String fromCode(String code) {
        return switch (code) {
            case EXEMPT_FROM_FEES_UNIT_NUMBER -> "zwolnieni";
            case RESIGNED_UNIT_NUMBER -> "rezygnacja";
            case REMOVED_UNIT_NUMBER -> "skreśleni";
            case DECEASED_UNIT_NUMBER -> "zmarli";
            default -> "nieznany";
        };
    }

    @Id
    private String id;

    private String name;

    public static Unit of(String line) {
        final String[] afterSplit = line.split(SEPARATOR, 2);
        return new Unit(afterSplit[0].trim(), afterSplit[1].trim());
    }

    public boolean sameAs(String unitNumber) {
        return StringUtils.equals(id, unitNumber);
    }

}
