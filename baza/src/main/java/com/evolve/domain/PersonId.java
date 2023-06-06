package com.evolve.domain;

import com.evolve.exception.AlpacaBusinessException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Comparator;

/**
 * PersonId cannot be used as @Id for Person class.
 * Nitrite is not ready yet for Java17 changes
 * @see <a href="https://stackoverflow.com/questions/69124839/jdk11-to-jdk12-migration-java-lang-nosuchfieldexception-modifiers">...</a>
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PersonId implements Comparable<PersonId>, Serializable{
    public static final String MAXIMUM_NUMBER_OF_PEOPLE_IN_GROUP = "999";

    private final String groupNumber; // number of the alphabetic group (01-24)
    private final String index; // number in the group (001-999)

    @Override
    public String toString() {
        return groupNumber + index;
    }

    @Override
    public int compareTo(PersonId o) {
        return Comparator.comparing(PersonId::getGroupNumber)
                .thenComparing(PersonId::getIndex)
                .compare(this, o);
    }

    public static PersonId of(String input) {
        return new PersonId(input.substring(0, 2), input.substring(2, 5));
    }

    public static PersonId nextId(PersonId current) {
        if (current.getIndex().equals("999")) {
            throw new AlpacaBusinessException("Maximum number of people in group reached: " + MAXIMUM_NUMBER_OF_PEOPLE_IN_GROUP);
        }
        return new PersonId(current.getGroupNumber(), String.format("%03d", Integer.parseInt(current.getIndex()) + 1));
    }

}
