package com.evolve.domain;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.Valid;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Person implements Serializable {

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

    @ElementCollection(targetClass = String.class) //, fetch = FetchType.EAGER
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> previousLastNames; // poprzednie nazwiska (panieńskie, przed zmianą nazwiska)

    private LocalDate dob; // urodzony/urodzona

    private String pesel;
    private String idNumber; // nr dowodu osobistego
    private Boolean retired; // na emeryturze
    private Boolean exemptFromFees; // zwolniony ze składek

    @Valid
    @ElementCollection(targetClass = PersonAddress.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PersonAddress> addresses ; // lista adresów

    @Valid
    @ElementCollection(targetClass = PersonContactData.class)
    @CollectionTable(name = "person_contact_data", joinColumns = @JoinColumn(name = "person_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PersonContactData> contactData;

    @ElementCollection(targetClass = BankAccount.class) //, fetch = FetchType.EAGER)
    @CollectionTable(name = "person_bank_accounts", joinColumns = @JoinColumn(name = "person_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BankAccount> bankAccounts;

    private PersonStatus status;

    @ElementCollection(targetClass = PersonStatusChange.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PersonStatusChange> statusChanges;

    @ElementCollection(targetClass = AuthorizedPerson.class) //, fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AuthorizedPerson> authorizedPersons; // if null nobody is authorized

    @ElementCollection(targetClass = Comment.class) //, fetch = FetchType.EAGER)
    @CollectionTable(name = "person_comments", joinColumns = @JoinColumn(name = "person_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Comment> comments; // notatki

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name="raw_data_key")
    @Column(name="raw_data_value")
    private Map<String, String> rawData;

    public void setStatusChanges(List<PersonStatusChange> statusChanges) {
        // TODO this is experimental
        this.statusChanges = List.copyOf(new TreeSet<>(statusChanges));

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
        private String comment;

        public PersonAddress(Address address, AddressType type) {
            super(address.getStreet(), address.getPostalCode(), address.getCity());
            this.type = type;
        }

        public PersonAddress(String street, String postCode, String city, AddressType type, String comment) {
            super(street, postCode, city);
            this.type = type;
            this.comment = comment;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum AddressType {
        HOME("Domowy"),
        MAILING("Korespondencyjny"),
        WORK("Służbowy"),
        OTHER("Inny");

        private final String name;

        @Override
        public String toString() {
            return this.name;
        }
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
        final PersonStatusDeducer personStatusDeducer = new PersonStatusDeducer(this);
        personStatusDeducer.addOrUpdateStatusChange(eventType, when);

        this.statusChanges = personStatusDeducer.getStatusChanges();
        this.status = personStatusDeducer.getStatus();
        personStatusDeducer.getDob().ifPresent(newDob -> this.dob = newDob);

    }

    public static Long calculateAge(LocalDate dob, PersonStatus status) {
        if (status != PersonStatus.ACTIVE) {
            return -1L;
        }

        return dob != null ? Period.between(dob, LocalDate.now()).getYears() : 0L;
    }

}
