package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.domain.Unit;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonDetailsController implements Initializable {
    private final PersonsService personsService;
    private final UnitsService unitsService;
    private final PersonListModel personListModel;

    private final ObjectProperty<LocalDate> birthDay = new SimpleObjectProperty<>();
    private final ObservableList<AddressEntry> addresses = FXCollections.observableArrayList();

    private Map<String, Unit> units;

    @FXML TextField idTextField;
    @FXML TextField firstNameTextField;
    @FXML TextField secondNameTextField;
    @FXML TextField lastNameTextField;
    @FXML RadioButton maleRadioButton;
    @FXML RadioButton femaleRadioButton;
    @FXML ToggleGroup genderToggleGroup;
    @FXML DatePicker dobPicker;
    @FXML TextField registryNumberTextField;
    @FXML TextField oldRegistryNumberTextField;
    @FXML TextField emailTextField;
    @FXML TextField unitNumberTextField;
    @FXML TableView<AddressEntry> addressesTable;
    @FXML TableColumn<AddressEntry, String> streetColumn;
    @FXML TableColumn<AddressEntry, String> postalCodeColumn;
    @FXML TableColumn<AddressEntry, String> cityColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialized - PersonDetails");

        this.units = unitsService.fetchAll();

        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    setPerson(newUser);
                });

    }

    public void setPerson(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return;
        }

        Person person = personsService.findById(personModel.getId());
        log.info("Person details: {}", person);

        idTextField.setText(person.getPersonId());
        firstNameTextField.setText(person.getFirstName());
        secondNameTextField.setText(person.getSecondName());
        lastNameTextField.setText(person.getLastName());
        emailTextField.setText(person.getEmail());
        registryNumberTextField.setText(Optional.ofNullable(person.getRegistryNum())
                .map(Object::toString).orElse(null));
        oldRegistryNumberTextField.setText(Optional.ofNullable(person.getOldRegistryNum())
                .map(Object::toString).orElse(null));

        genderToggleGroup.selectToggle(Person.Gender.MALE == person.getGender()
                ? maleRadioButton
                : femaleRadioButton);

        this.birthDay.setValue(person.getDob());
        Bindings.bindBidirectional(dobPicker.valueProperty(), birthDay);

        Optional.ofNullable(person.getUnitNumber())
                .ifPresentOrElse(unitNumber -> this.unitNumberTextField.setText(
                        getUnitNumber(unitNumber)),
                        () -> this.unitNumberTextField.clear());

        addresses.clear();
        emptyIfNull(person.getAddresses())
                .forEach(address -> {
                    addresses.add(new AddressEntry(address));
                });

        addressesTable.setItems(addresses);
    }

    private String getUnitNumber(String unitNumber) {
        if (units.containsKey(unitNumber)) {
            return unitNumber + " - " + units.get(unitNumber).getName();
        }
        return unitNumber + " - ???";
    }

    @AllArgsConstructor
    @Getter
    public static class AddressEntry {
        private String street;
        private String postalCode;
        private String city;

        public AddressEntry(Person.PersonAddress personAddress) {
            this(personAddress.getStreet(), personAddress.getPostalCode(), personAddress.getCity());
        }
    }
}
