package com.evolve.gui.person.address;

import com.evolve.domain.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@ToString
public class AddressEntry {

    @Getter
    private Person.PersonAddress personAddress;


    public String getStreet() {
        return personAddress.getStreet();
    }

    public String getPostalCode() {
        return personAddress.getPostalCode();
    }

    public String getCity() {
        return personAddress.getCity();
    }

    public Person.AddressType getType() {
        return personAddress.getType();
    }

}