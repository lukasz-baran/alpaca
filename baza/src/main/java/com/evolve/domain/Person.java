package com.evolve.domain;

import lombok.*;
import org.dizitart.no2.repository.annotations.Id;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    @Id
    private String personId;

    private String unitNumber; // 95 - nie płaci składek

    private Integer registryNum; // numer kartoteki
    private Integer oldRegistryNum; // numer starej kartoteki


    private String firstName; // imię
    private String secondName; // drugie imię
    private String lastName; // nazwisko
    private Gender gender;

    private List<String> previousLastNames; // poprzednie nazwiska (panieńskie, przed zmianą nazwiska)

    private LocalDate dob; // urodzony/urodzona
    private LocalDate memberSince; // data założenia konta

    private List<PersonAddress> addresses; // lista adresów

    private List<String> phoneNumbers;
    private String email;

    private List<BankAccount> bankAccounts;

    private PersonStatusDetails status;

    private List<AuthorizedPerson> authorizedPersons; // if null nobody is authorized

    private List<Comment> comments; // notatki

    private Map<String, Object> rawData;

    public enum Gender {
        MALE,
        FEMALE
    }

    @Getter
    @NoArgsConstructor
    public static class PersonAddress extends Address {

        private AddressType type;

        public PersonAddress(Address address, AddressType type) {
            super(address.getStreet(), address.getPostalCode(), address.getCity());
            this.type = type;
        }

        public PersonAddress(String street, String postCode, String city, AddressType type) {
            super(street, postCode, city);
            this.type = type;
        }
    }

    public enum AddressType {
        HOME,
        MAILING,
        OTHER
    }


    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    @Builder
    @NoArgsConstructor
    public static class AuthorizedPerson {
        private String firstName;
        private String lastName;
        private String relation; // żona, mąż, syn, matka, córka, synowie
        private String phone;
        private Address address;
        private String comment;
    }

}
