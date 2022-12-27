package com.evolve.domain;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Address implements Serializable {

    private String street;
    private String postalCode;
    private String city;

    public static Address of(String street, String postalCode, String city) {
        return new Address(street, postalCode, city);
    }

}
