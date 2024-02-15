package com.evolve.gui.person.details;

import com.evolve.EditPersonDataCommand;
import com.evolve.alpaca.util.LocalDateStringConverter;
import com.evolve.alpaca.validation.ValidationException;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.RegistryNumber;
import com.evolve.domain.Unit;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import com.evolve.gui.components.GenderComboboxController;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.gui.person.UnitNumberItem;
import com.evolve.gui.person.address.PersonAddressesController;
import com.evolve.gui.person.authorizedPerson.AuthorizedPersonsController;
import com.evolve.gui.person.bankAccounts.PersonBankAccountsController;
import com.evolve.gui.person.contactDetails.PersonContactDataController;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import com.evolve.gui.person.status.PersonStatusController;
import com.evolve.services.PersonEditService;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URL;
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

    @FXML private final FxControllerAndView<GenderComboboxController, HBox> personGender;
    @FXML final FxControllerAndView<AuthorizedPersonsController, AnchorPane> authorizedController;
    @FXML final FxControllerAndView<PersonContactDataController, AnchorPane> phoneNumbersController;
    @FXML final FxControllerAndView<PersonAddressesController, AnchorPane> personAddresses;
    @FXML final FxControllerAndView<PersonStatusController, VBox> personStatusController;
    @FXML final FxControllerAndView<PersonBankAccountsController, VBox> personBankAccountsController;

    private final ObjectProperty<Person> originalPerson = new SimpleObjectProperty<>();

    public Button btnSave;
    public Button btnCancel;

    @FXML Text textPersonStatus; // person status is displayed in the label

    @FXML TextField idTextField;
    @FXML TextField firstNameTextField;
    @FXML TextField secondNameTextField;
    @FXML TextField lastNameTextField;
    @FXML TextField dobTextField;

    @FXML TextField registryNumberTextField;
    @FXML TextField oldRegistryNumberTextField;

    @FXML CheckBox retiredCheckBox;
    @FXML CheckBox exemptFromFeesCheckBox;

    @FXML ComboBox<UnitNumberItem> unitNumberComboBox;

    @FXML TextField peselTextField;
    @FXML TextField idNumberTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("initialized - PersonDetails");

        final ObservableList<UnitNumberItem> units = FXCollections.observableArrayList();
        for (Unit unit : unitsService.fetchList()) {
            units.add(new UnitNumberItem(unit.getId(), unit.getName()));
        }
        unitNumberComboBox.setItems(units);

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    setPerson(newUser);
                });
    }

    private void setPerson(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return;
        }

        final Person person = personsService.findById(personModel.getId());
        log.info("Person details: {}", person);

        originalPerson.setValue(person);

        idTextField.setText(person.getPersonId());
        firstNameTextField.setText(person.getFirstName());
        secondNameTextField.setText(person.getSecondName());
        lastNameTextField.setText(person.getLastName());

        phoneNumbersController.getController().setPersonContactData(person.getContactData());

        registryNumberTextField.setText(
                Optional.ofNullable(person.getRegistryNumber())
                        .map(RegistryNumber::getRegistryNum)
                        .map(Object::toString).orElse(null));
        oldRegistryNumberTextField.setText(
                Optional.ofNullable(person.getOldRegistryNumber())
                        .flatMap(RegistryNumber::getNumber)
                        .map(Object::toString).orElse(null));

        retiredCheckBox.setSelected(BooleanUtils.isTrue(person.getRetired()));
        exemptFromFeesCheckBox.setSelected(BooleanUtils.isTrue(person.getExemptFromFees()));

        personGender.getController().setPersonGender(person);

        dobTextField.setText(LocalDateStringConverter.localDateToString(person.getDob()));
        dobTextField.setEditable(false); // DOB is not editable using during person details edition
        dobTextField.setTooltip(new Tooltip("Data urodzenia jest ustalana na podstawie listy statusów."));

        peselTextField.setText(person.getPesel());
        idNumberTextField.setText(person.getIdNumber());

        unitsService.fetchList()
            .stream()
            .filter(unit -> unit.getId().equals(person.getUnitNumber()))
            .findFirst()
            .map(unit -> new UnitNumberItem(unit.getId(), unit.getName()))
            .ifPresentOrElse(unitNumber -> this.unitNumberComboBox.getSelectionModel().select(unitNumber),
                    () -> this.unitNumberComboBox.getSelectionModel().clearSelection());
        unitNumberComboBox.setDisable(true);

        personAddresses.getController().setPersonAddresses(person.getAddresses());

        authorizedController.getController().setAuthorizedPersons(person.getAuthorizedPersons());

        setPersonStatus(person);

        personBankAccountsController.getController().setPersonBankAccounts(person.getBankAccounts());

        btnCancel.setCancelButton(true);
    }

    @Override
    public void setEditable(boolean editable) {
        btnSave.setDisable(!editable);
        btnCancel.setDisable(!editable);

        firstNameTextField.setEditable(editable);
        lastNameTextField.setEditable(editable);
        secondNameTextField.setEditable(editable);

        // FIXME person gender cannot be edited!
        personGender.getController().setEditable(editable);

        unitNumberComboBox.setDisable(!editable);

        registryNumberTextField.setEditable(editable);
        oldRegistryNumberTextField.setEditable(editable);

        retiredCheckBox.setDisable(!editable);
        exemptFromFeesCheckBox.setDisable(!editable);

        personAddresses.getController().setEditable(editable);
        authorizedController.getController().setEditable(editable);
        phoneNumbersController.getController().setEditable(editable);
        personStatusController.getController().setEditable(editable);
        personBankAccountsController.getController().setEditable(editable);

        peselTextField.setEditable(editable);
        idNumberTextField.setEditable(editable);
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

    private void setPersonStatus(Person person) {
        textPersonStatus.setText(Optional.ofNullable(person.getStatus())
                        .orElse(PersonStatus.ACTIVE)
                                .getName());
        personStatusController.getController().setPerson(person);
    }

    private void persistChanges() {
        final PersonDetailsChange personDetailsEditionProcess = new PersonDetailsChange(originalPerson);

        final EditPersonDataCommand command = personDetailsEditionProcess.buildCommand(this);

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
