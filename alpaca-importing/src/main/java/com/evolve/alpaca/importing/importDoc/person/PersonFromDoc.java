package com.evolve.alpaca.importing.importDoc.person;

import com.evolve.domain.PersonStatusDetails;
import com.evolve.domain.RegistryNumber;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class PersonFromDoc {

    private RegistryNumber numerKartoteki; // numer porządkowy nie wszyscy mają
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

    public String getPersonId() {
        return numerGrupy + index;
    }


}
