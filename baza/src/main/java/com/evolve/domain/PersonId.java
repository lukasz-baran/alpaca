package com.evolve.domain;

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
    private final String groupNumber; // numer grupy alfabetycznej (01-24)
    private final String index; // numer porzadkowy w grupie

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

}
