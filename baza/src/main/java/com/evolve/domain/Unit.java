package com.evolve.domain;

import lombok.AllArgsConstructor;
import org.dizitart.no2.repository.annotations.Id;

/**
 * Unit represents: medical institution or other type of assignements
 */
@AllArgsConstructor
public class Unit {
    @Id
    private String id;
    private String name;



}
