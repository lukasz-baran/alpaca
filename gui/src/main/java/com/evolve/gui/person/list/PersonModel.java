package com.evolve.gui.person.list;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.RegistryNumber;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.BooleanUtils;

import java.time.LocalDate;
import java.util.Optional;

import static com.evolve.domain.Person.calculateAge;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("unused")
@ToString
public class PersonModel {

    private final SimpleStringProperty id;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final ObjectProperty<LocalDate> dob;
    private final SimpleLongProperty age;
    private final SimpleStringProperty status;
    private final SimpleLongProperty registryNumber;
    @Getter
    private final SimpleBooleanProperty retired;
    @Getter
    private final SimpleBooleanProperty exemptFromFees;
    @Getter
    private final PersonStatus personStatus;

    PersonModel(String id, String firstName, String lastName, LocalDate dob, PersonStatus status,
                Long registryNumber, Boolean retired, Boolean exemptFromFees) {
        this.id = new SimpleStringProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.dob = new SimpleObjectProperty<>(dob);
        this.age = new SimpleLongProperty(calculateAge(dob, status));
        this.status = new SimpleStringProperty(status.toString());
        this.registryNumber = new SimpleLongProperty(registryNumber);
        this.retired = new SimpleBooleanProperty(BooleanUtils.isTrue(retired));
        this.exemptFromFees = new SimpleBooleanProperty(BooleanUtils.isTrue(exemptFromFees));
        this.personStatus = status;
    }

    PersonModel(PersonListView person) {
        this(person.personId(), person.firstName(), person.lastName(), person.dob(), person.status(),
                person.getRegistryNumber().orElse(0L), person.retired(), person.exemptFromFees());
    }

    public String getId() {
        return this.id.get();
    }
    public void setId(String id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public LocalDate getDob() {
        return dob.get();
    }
    public void setDob(LocalDate dob) {
        this.dob.set(dob);
    }

    public Long getAge() {
        return age.get();
    }
    public void setAge(Long age) {
        this.age.set(age);
    }

    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    public Long getRegistryNumber() {
        return registryNumber.get();
    }
    public void setRegistryNumber(Long registryNumber) {
        this.registryNumber.set(registryNumber);
    }

    public void update(Person updatedPerson) {
        this.firstName.set(updatedPerson.getFirstName());
        this.lastName.set(updatedPerson.getLastName());
        this.dob.set(updatedPerson.getDob());

        final Optional<PersonStatus> maybePersonStatus = Optional.ofNullable(updatedPerson.getStatus());
        this.age.set(calculateAge(updatedPerson.getDob(), maybePersonStatus.orElse(null)));
        maybePersonStatus.ifPresent(status -> this.status.set(status.getName()));

        final Optional<Integer> maybeRegistryNumber = Optional.ofNullable(updatedPerson.getRegistryNumber())
                .map(RegistryNumber::getRegistryNum);
        maybeRegistryNumber.ifPresentOrElse(this.registryNumber::set,
                () -> this.registryNumber.set(0L));

        this.retired.set(BooleanUtils.isTrue(updatedPerson.getRetired()));
        this.exemptFromFees.set(BooleanUtils.isTrue(updatedPerson.getExemptFromFees()));
    }

    public boolean matches(String filteredText) {
        if (filteredText == null || filteredText.isEmpty()) {
            return true;
        }

        final String lowerCaseFilter = filteredText.toLowerCase();
        if (trimToEmpty(getFirstName()).toLowerCase().contains(lowerCaseFilter)) {
            return true;
        }

        if (trimToEmpty(getLastName()).toLowerCase().contains(lowerCaseFilter)) {
            return true;
        }

        return trimToEmpty(getId()).toLowerCase().contains(lowerCaseFilter);
    }

}
