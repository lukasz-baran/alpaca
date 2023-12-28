package com.evolve.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
public class BankAccount {

    private String number;

    public static BankAccount of(String number) {
        return new BankAccount(number);
    }

//    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
//    private List<String> notes;

}
