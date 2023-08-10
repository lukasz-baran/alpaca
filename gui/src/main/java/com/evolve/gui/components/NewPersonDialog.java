package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;
import com.evolve.domain.RegistryNumber;
import com.evolve.domain.Unit;
import com.evolve.gui.DialogWindow;
import com.evolve.importing.importDbf.deducers.PersonGenderDeducer;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
public class NewPersonDialog extends DialogWindow<Person> {

    private final PersonsService personsService;
    private final UnitsService unitsService;

    public NewPersonDialog(PersonsService personsService, UnitsService unitsService) {
        super("Nowa osoba", "Podaj dane osobowe nowej osoby. Numer ID zostanie wygenerowany automatycznie");
        this.personsService = personsService;
        this.unitsService = unitsService;
    }

    @Override
    public Optional<Person> showDialog(Window window) {
        final Dialog<Person> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("Imię");
        final TextField lastNameTextField = new TextField();
        lastNameTextField.setPromptText("Nazwisko");

        final TextField personIdTextField = new TextField();
        personIdTextField.setPromptText("ID");
        personIdTextField.setEditable(false);

        final SecureLocalDateStringConverter joinedConverter = new SecureLocalDateStringConverter();
        final DatePicker joinedDatePicker = new DatePicker();
        joinedDatePicker.setConverter(joinedConverter);
        joinedDatePicker.setPromptText("Data dołączenia");
        joinedDatePicker.getEditor().setOnKeyTyped(new DatePickerKeyEventHandler(joinedConverter, joinedDatePicker));

        final SecureLocalDateStringConverter dobConverter = new SecureLocalDateStringConverter();
        final DatePicker dobDatePicker = new DatePicker();
        dobDatePicker.setConverter(dobConverter);
        dobDatePicker.setPromptText("Data urodzenia");
        dobDatePicker.getEditor().setOnKeyTyped(new DatePickerKeyEventHandler(dobConverter, dobDatePicker));

        ObservableList<UnitNumberItem> units = FXCollections.observableArrayList();
        for (Unit unit : unitsService.fetchList()) {
            units.add(new UnitNumberItem(unit.getId(), unit.getName()));
        }
        final ComboBox<UnitNumberItem> unitNumberCombo = new ComboBox<>(units);

        final ComboBox<Person.Gender> genderCombo = new ComboBox<>(
                new ImmutableObservableList<>(Person.Gender.FEMALE, Person.Gender.MALE));

        final TextField registryNumberTextField = new TextField();
        registryNumberTextField.setPromptText("Numer kartoteki");

        // first column
        grid.add(new Label("Imię:"), 0, 0);
        grid.add(firstNameTextField, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(lastNameTextField, 1, 1);

        grid.add(new Label("Dołączył(a):"), 0, 2);
        grid.add(joinedDatePicker, 1, 2);

        grid.add(new Label("Jednostka:"), 0, 3);
        grid.add(unitNumberCombo, 1, 3);

        // second column
        grid.add(new Label("ID:"), 2, 0);
        grid.add(personIdTextField, 3, 0);

        grid.add(new Label("Płeć:"), 2, 1);
        grid.add(genderCombo, 3, 1);

        grid.add(new Label("Data urodzenia:"), 2, 2);
        grid.add(dobDatePicker, 3, 2);

        grid.add(new Label("Kartoteka:"), 2, 3);
        grid.add(registryNumberTextField, 3, 3);


        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePersonGenderBasedOnFirstName(firstNameTextField, genderCombo);
            validateSaveButton(saveButton, personIdTextField, firstNameTextField, lastNameTextField);
        });
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            regeneratePersonId(personIdTextField, lastNameTextField);
            validateSaveButton(saveButton, personIdTextField, firstNameTextField, lastNameTextField);
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(firstNameTextField::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {

                final String unitNumber = Optional.ofNullable(unitNumberCombo.getValue())
                        .map(item -> item.unitNumber)
                        .orElse(null);

                final Person newPerson = Person.builder()
                        .personId(personIdTextField.getText())
                        .firstName(StringUtils.capitalize(firstNameTextField.getText().trim()))
                        .lastName(StringUtils.capitalize(lastNameTextField.getText().trim()))
                        .registryNumber(RegistryNumber.onlyNewRegistryNumber(registryNumberTextField.getText().trim()))
                        .gender(genderCombo.getValue())
                        .unitNumber(unitNumber)
                        .build();

                final LocalDate dob = dobDatePicker.getValue();
                if (dob != null) {
                    newPerson.updatePersonDob(dob);
                }

                final LocalDate joined = joinedDatePicker.getValue();
                if (joined != null) {
                    newPerson.addOrUpdateStatusChange(PersonStatusChange.EventType.JOINED, joined);
                }

                return newPerson;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    void updatePersonGenderBasedOnFirstName(TextField firstNameTextField, ComboBox<Person.Gender> genderCombo) {
        final String firstName = firstNameTextField.getText().trim();
        if (firstName.isEmpty()) {
            genderCombo.setValue(null);
            return;
        }

        genderCombo.setValue(PersonGenderDeducer.getGender(firstName));
    }

    void regeneratePersonId(TextField personIdTextField, TextField lastNameTextField) {
        personIdTextField.setText("");
        personsService.findNextPersonId(lastNameTextField.getText().trim())
                .ifPresent(personIdTextField::setText);
    }

    void validateSaveButton(
            Node saveButton, TextField personIdTextField, TextField firstNameTextField, TextField lastNameTextField) {
        final boolean disable = personIdTextField.getText().trim().isEmpty()
                || firstNameTextField.getText().trim().isEmpty()
                || lastNameTextField.getText().trim().isEmpty();

        saveButton.setDisable(disable);
    }


    public record UnitNumberItem(String unitNumber, String unitName) {

        @Override
        public String toString() {
            return unitNumber + " - " + unitName;
        }
    }

    @RequiredArgsConstructor
    static class DatePickerKeyEventHandler implements EventHandler<KeyEvent> {
        private final SecureLocalDateStringConverter converter;
        private final DatePicker datePicker;

        @Override
        public void handle(KeyEvent event) {
            final String textValue = datePicker.getEditor().getText();
            datePicker.setStyle("");
            converter.fromString(textValue);
            if (StringUtils.isNotBlank(textValue) && converter.hasParseError()) {
                datePicker.setStyle("-fx-border-color: red");
            }

        }
    }

}
