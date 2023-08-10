package com.evolve.gui;

import com.evolve.EditPersonDataCommand;
import com.evolve.domain.Person;
import com.evolve.domain.Unit;
import com.evolve.gui.components.*;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.gui.person.authorizedPerson.AuthorizedPersonsController;
import com.evolve.gui.person.phoneNumber.PhoneNumbersController;
import com.evolve.gui.person.status.PersonStatusController;
import com.evolve.services.PersonEditService;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import com.evolve.validation.ValidationException;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("person-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonDetailsController extends EditableGuiElement
        implements Initializable {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PersonsService personsService;
    private final PersonEditService personEditService;
    private final UnitsService unitsService;
    private final PersonListModel personListModel;
    private final StageManager stageManager;

    @FXML
    private final FxControllerAndView<GenderComboboxController, HBox> personGender;
    @FXML
    private final FxControllerAndView<AuthorizedPersonsController, AnchorPane> authorizedController;

    @FXML
    private final FxControllerAndView<DateEditController, HBox> dobPicker;

    @FXML
    private final FxControllerAndView<PhoneNumbersController, AnchorPane> phoneNumbersController;

    @FXML
    private final FxControllerAndView<PersonAddressesController, AnchorPane> personAddresses;
    @FXML
    private final FxControllerAndView<RegistryNumbersController, HBox> registryNumbers;
    @FXML
    private final FxControllerAndView<PersonStatusController, VBox> personStatusController;

    public Button btnSave;
    public Button btnCancel;

    private Map<String, Unit> units;

    @FXML TextField idTextField;
    @FXML TextField firstNameTextField;
    @FXML TextField secondNameTextField;
    @FXML TextField lastNameTextField;

    @FXML TextField emailTextField;
    @FXML TextField unitNumberTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialized - PersonDetails");

        this.units = unitsService.fetchMap();

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

        phoneNumbersController.getController().setPhoneNumbers(person.getPhoneNumbers());

        registryNumbers.getController().setPerson(person);
        personGender.getController().setPersonGender(person);

        dobPicker.getController().setDate(person.getDob());

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


    @Override
    public void setEditable(boolean editable) {
        btnSave.setDisable(!editable);
        btnCancel.setDisable(!editable);

        firstNameTextField.setEditable(editable);
        lastNameTextField.setEditable(editable);
        secondNameTextField.setEditable(editable);
        emailTextField.setEditable(editable);

        // FIXME person gender cannot be edited!
        personGender.getController().setEditable(editable);

        dobPicker.getController().setEditable(editable);

        personAddresses.getController().setEditable(editable);
        authorizedController.getController().setEditable(editable);
        phoneNumbersController.getController().setEditable(editable);
        personStatusController.getController().setEditable(editable);
    }


    public void saveButtonClicked(ActionEvent actionEvent) {
        // display alert asking for confirmation if confirmed, send command to update person data and close the window  if not confirmed, revert changes on view and close the window
        boolean result = stageManager.displayConfirmation("Zapisać zmiany?");
        if (result) {
            persistChanges();
        } else {
            revertChanges();
        }
    }

    public void cancelButtonClicked(ActionEvent actionEvent) {
        revertChanges();
    }

    private void persistChanges() {
        final EditPersonDataCommand command = new EditPersonDataCommand(
                idTextField.getText(),
                firstNameTextField.getText(),
                lastNameTextField.getText(),
                secondNameTextField.getText(),
                emailTextField.getText(),
                phoneNumbersController.getController().getNumbers(),
                dobPicker.getController().getDate(),
                personAddresses.getController().getPersonAddresses(),
                authorizedController.getController().getAuthorizedPersons()
                );

        log.info("Update person data: {}", command);


        final Person savedPerson;
        try {
            savedPerson = personEditService.editPerson(command);
        } catch (ValidationException e) {
            log.error("Error while updating person data", e);
            stageManager.displayWarning("Błąd walidacji: " +e.getErrorMessages());
            return;
        }

        setEditable(false);
        // update person gender:
        personGender.getController().setPersonGender(savedPerson);

        applicationEventPublisher.publishEvent(new PersonEditionFinishedEvent(this, savedPerson));
    }

    private void revertChanges() {
        final PersonModel personModel = personListModel.getCurrentPersonProperty().getValue();

        // revert changes on view
        setPerson(personModel);
        setEditable(false);

        applicationEventPublisher.publishEvent(new PersonEditionFinishedEvent(this, null));
    }

}
