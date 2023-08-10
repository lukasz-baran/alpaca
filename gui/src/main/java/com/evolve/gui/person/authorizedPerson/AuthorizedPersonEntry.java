package com.evolve.gui.person.authorizedPerson;

import com.evolve.domain.Person;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class AuthorizedPersonEntry {

    @Getter
    private Person.AuthorizedPerson authorizedPerson;

    public String getFirstName() {
        return authorizedPerson.getFirstName();
    }

    public String getLastName() {
        return authorizedPerson.getLastName();
    }

    public String getRelation() {
        return authorizedPerson.getRelation();
    }

}