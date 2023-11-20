package com.evolve.domain;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Person implements Serializable {
    public static final String EMAIL_IS_NOT_VALID = "Email address is not valid";

    @javax.persistence.Id
    private String personId;

    private String unitNumber; // 95 - nie płaci składek

    @Embedded
    @AttributeOverrides( {@AttributeOverride(name="registryNum", column = @Column(name="registry_num") )} )
    private RegistryNumber registryNumber;

    @Embedded
    @AttributeOverrides( {@AttributeOverride(name="registryNum", column = @Column(name="old_registry_num") )} )
    private RegistryNumber oldRegistryNumber;

    private String firstName; // imię
    private String secondName; // drugie imię
    private String lastName; // nazwisko

    private Gender gender;

    @ElementCollection(targetClass = String.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> previousLastNames; // poprzednie nazwiska (panieńskie, przed zmianą nazwiska)

    private LocalDate dob; // urodzony/urodzona
    private LocalDate memberSince; // data założenia konta

    @Valid
    @ElementCollection(targetClass = PersonAddress.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PersonAddress> addresses ; // lista adresów

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> phoneNumbers;

    @Email(message = EMAIL_IS_NOT_VALID)
    private String email;

    @ElementCollection(targetClass = BankAccount.class) //, fetch = FetchType.EAGER)
    @CollectionTable(name = "person_bank_accounts", joinColumns = @JoinColumn(name = "person_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BankAccount> bankAccounts;

    @Embedded
    private PersonStatusDetails status;

    @ElementCollection(targetClass = PersonStatusChange.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PersonStatusChange> statusChanges;

    @ElementCollection(targetClass = AuthorizedPerson.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AuthorizedPerson> authorizedPersons; // if null nobody is authorized

    @ElementCollection(targetClass = Comment.class) //, fetch = FetchType.EAGER)
    @CollectionTable(name = "person_comments", joinColumns = @JoinColumn(name = "person_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
//    @AttributeOverrides({
//            @AttributeOverride(name = "addressLine1", column = @Column(name = "house_number")),
//            @AttributeOverride(name = "addressLine2", column = @Column(name = "street"))
//    })
    private List<Comment> comments; // notatki

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="raw_data_key")
    @Column(name="raw_data_value")
    //@CollectionTable(name="person_raw_data", joinColumns=@JoinColumn(name="person_id"))
    private Map<String, String> rawData;

    public void setStatusChanges(List<PersonStatusChange> statusChanges) {
        this.statusChanges = statusChanges;
        emptyIfNull(statusChanges).stream()
                .filter(statusChange -> statusChange.getEventType() == PersonStatusChange.EventType.BORN)
                .findFirst()
                .ifPresent(dobStatusChange -> this.dob = dobStatusChange.getWhen());
    }

    @Getter
    @RequiredArgsConstructor
    public enum Gender {
        MALE("Mężczyzna"),
        FEMALE("Kobieta");

        private final String name;

        @Override
        public String toString() {
            return this.name;
        }
    }

    @Getter
    @NoArgsConstructor
    @Embeddable
    @EqualsAndHashCode(callSuper = true)
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
    @Embeddable
    public static class AuthorizedPerson {
        private String firstName;
        private String lastName;
        private String relation; // żona, mąż, syn, matka, córka, synowie
        private String phone;
        private Address address;
        private String comment;
    }


    public void addOrUpdateStatusChange(PersonStatusChange.EventType eventType, LocalDate when) {
        if (this.statusChanges == null) {
            this.statusChanges = new ArrayList<>();
        }
        this.statusChanges
                .stream()
                .filter(personStatusChange -> personStatusChange.getEventType() == eventType)
                .findFirst()
                .ifPresentOrElse(statusChange -> {
                            statusChange.setWhen(when);
                            statusChange.setEventType(eventType);
                        },
                        () -> addNewStatusChange(eventType, when));

        this.status = PersonStatusDetails.basedOnStatusChange(this.statusChanges);
    }

    private void addNewStatusChange(PersonStatusChange.EventType eventType, LocalDate when) {
        PersonStatusChange newPersonStatusChange = PersonStatusChange.builder()
                .eventType(eventType)
                .when(when)
                .build();

        if (eventType == PersonStatusChange.EventType.BORN) {
            this.dob = when;
            this.statusChanges.add(0, newPersonStatusChange);
        } else {
            this.statusChanges.add(newPersonStatusChange);
        }
    }

}
