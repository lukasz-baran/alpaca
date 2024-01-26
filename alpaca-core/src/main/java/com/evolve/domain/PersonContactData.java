package com.evolve.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Embeddable
@ToString
public class PersonContactData {

    @NotBlank
    private String data;
    @NotNull
    private ContactType type;
    private String comment;

    public static PersonContactData phone(String phoneNumber) {
        return new PersonContactData(phoneNumber, ContactType.PHONE);
    }

    public static PersonContactData email(String email) {
        return new PersonContactData(email, ContactType.EMAIL);
    }

    public PersonContactData(String phoneNumber, ContactType contactType) {
        this(phoneNumber, contactType, null);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ContactType {
        PHONE("Telefon"),
        EMAIL("Email");

        private final String description;
    }

    public static Optional<String> findEmail(List<PersonContactData> contactDataList) {
        return emptyIfNull(contactDataList)
                .stream()
                .filter(contact -> contact.getType() == ContactType.EMAIL)
                .map(contact -> contact.data)
                .findFirst();
    }

}
