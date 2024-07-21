package com.evolve.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
@Setter
@MappedSuperclass
public class Address implements Serializable {
    private String street;
    private String postalCode;
    private String city;

    public static Address of(String street, String postalCode, String city) {
        return new Address(street, postalCode, city);
    }

    public static String toConcatenatedAddress(Address address) {
        return String.join(" ",
                trimToEmpty(address.getStreet()),
                trimToEmpty(address.getPostalCode()),
                trimToEmpty(address.getCity()));
    }

}
