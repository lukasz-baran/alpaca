package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class PersonId {
    private String groupNumber; // numer grupy alfabetycznej (01-24)
    private String index; // numer porzadkowy w grupie

}
