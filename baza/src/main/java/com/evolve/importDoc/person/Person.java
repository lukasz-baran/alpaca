package com.evolve.importDoc.person;

import com.evolve.domain.PersonStatusDetails;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class Person {

    private KartotekaId numerKartoteki; // numer porządkowy nie wszyscy mają
    private String numerJednostki; // 95 - nie płaci składek
    private String numerGrupy; // numer grupy alfabetycznej (01-24)
    private String index; // numer porzadkowy w grupie

    private String numerStarejKartoteki;

    private String lastName;
    private String firstName;
    private String secondName;

    private String line;

    private PersonStatusDetails statusDetails;

    public boolean isCorrect() {
        return StringUtils.isNotBlank(lastName) && StringUtils.isNotBlank(firstName);
    }




}
