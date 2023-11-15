package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
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

    @javax.persistence.Id
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
