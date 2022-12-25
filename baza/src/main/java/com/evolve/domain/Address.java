package com.evolve.domain;

import lombok.*;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class Address implements Serializable {

    private String street;
    private String postCode;
    private String city;

    public static Address of(String street, String cityCode) {
        // TODO split cityCode into two
        return new Address(street, cityCode, cityCode);
    }

}
