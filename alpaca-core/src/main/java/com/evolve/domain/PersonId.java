package com.evolve.domain;

import com.evolve.exception.AlpacaBusinessException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

/**
 * PersonId cannot be used as @Id for Person class.
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PersonId implements Comparable<PersonId>, Serializable {
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

    public static Optional<String> firstId(Optional<String> maybeGroupNumber) {
        return maybeGroupNumber.map(groupNumber -> new PersonId(groupNumber, "001")).map(PersonId::toString);
    }


}
