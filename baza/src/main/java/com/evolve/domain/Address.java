package com.evolve.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
@Setter
@MappedSuperclass
public class Address implements Serializable {
    public static final String CITY_IS_NOT_VALID = "City cannot be empty";
    public static final String POSTAL_CODE_IS_NOT_VALID = "Postal code cannot be empty";
    public static final String STREET_IS_NOT_VALID = "Street cannot be empty";

    //@NotBlank(message = STREET_IS_NOT_VALID)
    private String street;
    //@NotBlank(message = POSTAL_CODE_IS_NOT_VALID)
    private String postalCode;
    //@NotBlank(message = CITY_IS_NOT_VALID)
    private String city;

    public static Address of(String street, String postalCode, String city) {
        return new Address(street, postalCode, city);
    }

}
