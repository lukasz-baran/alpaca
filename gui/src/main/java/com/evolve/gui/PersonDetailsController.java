package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.domain.Unit;
import com.evolve.gui.components.*;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("person-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonDetailsController implements Initializable {
    private final PersonsService personsService;
    private final UnitsService unitsService;
    private final PersonListModel personListModel;

    @FXML
    private final FxControllerAndView<GenderComboboxController, HBox> personGender;
    @FXML
    private final FxControllerAndView<AuthorizedPersonsController, AnchorPane> authorizedController;
    @FXML
    private final FxControllerAndView<PersonAddressesController, AnchorPane> personAddresses;
    @FXML
    private final FxControllerAndView<RegistryNumbersController, HBox> registryNumbers;
    @FXML
    private final FxControllerAndView<PersonStatusController, VBox> personStatusController;

    private final ObjectProperty<LocalDate> birthDay = new SimpleObjectProperty<>();

    private Map<String, Unit> units;

    @FXML TextField idTextField;
    @FXML TextField firstNameTextField;
    @FXML TextField secondNameTextField;
    @FXML TextField lastNameTextField;
    @FXML DatePicker dobPicker;
//    @FXML TextField registryNumberTextField;
//    @FXML TextField oldRegistryNumberTextField;
    @FXML TextField emailTextField;
    @FXML TextField unitNumberTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialized - PersonDetails");

        this.units = unitsService.fetchAll();

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

        registryNumbers.getController().setPerson(person);
        personGender.getController().setPersonGender(person);

        this.birthDay.setValue(person.getDob());
        Bindings.bindBidirectional(dobPicker.valueProperty(), birthDay);

        Optional.ofNullable(person.getUnitNumber())
                .ifPresentOrElse(unitNumber -> this.unitNumberTextField.setText(
                        getUnitNumber(unitNumber)),
                        () -> this.unitNumberTextField.clear());

        personAddresses.getController().setPersonAddresses(person.getAddresses());

        authorizedController.getController().setAuthorizedPersons(person.getAuthorizedPersons());

        personStatusController.getController().setPerson(person);
    }

    private String getUnitNumber(String unitNumber) {
        if (units.containsKey(unitNumber)) {
            return unitNumber + " - " + units.get(unitNumber).getName();
        }
        return unitNumber + " - ???";
    }


}
