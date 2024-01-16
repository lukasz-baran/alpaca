package com.evolve.gui.components;

import com.evolve.alpaca.util.DatePickerKeyEventHandler;
import com.evolve.alpaca.util.LocalDateStringConverter;
import com.evolve.domain.*;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.person.UnitNumberItem;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
public class NewPersonDialog extends DialogWindow<Person> {

    private final PersonsService personsService;
    private final UnitsService unitsService;
    private final TextField firstNameTextField = new TextField();
    private final TextField lastNameTextField = new TextField();
    private final TextField personIdTextField = new TextField();

    public NewPersonDialog(PersonsService personsService, UnitsService unitsService) {
        super("Nowa osoba", "Podaj dane osobowe nowej osoby. Numer ID zostanie wygenerowany automatycznie");
        this.personsService = personsService;
        this.unitsService = unitsService;
    }

    @Override
    public Optional<Person> showDialog(Window window) {
        final Dialog<Person> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        firstNameTextField.setPromptText("Imię");
        lastNameTextField.setPromptText("Nazwisko");

        personIdTextField.setPromptText("ID");
        personIdTextField.setEditable(false);

        final LocalDateStringConverter joinedConverter = new LocalDateStringConverter();
        final DatePicker joinedDatePicker = new DatePicker();
        joinedDatePicker.setConverter(joinedConverter);
        joinedDatePicker.setPromptText("Data dołączenia");
        joinedDatePicker.getEditor().setOnKeyTyped(new DatePickerKeyEventHandler(joinedConverter, joinedDatePicker));

        final LocalDateStringConverter dobConverter = new LocalDateStringConverter();
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
                FXCollections.observableArrayList(Person.Gender.FEMALE, Person.Gender.MALE));

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
            validateSaveButton(saveButton);
        });
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            regeneratePersonId(personIdTextField, lastNameTextField);
            validateSaveButton(saveButton);
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(firstNameTextField::requestFocus);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {

                final String unitNumber = Optional.ofNullable(unitNumberCombo.getValue())
                        .map(UnitNumberItem::unitNumber)
                        .orElse(null);

                final Person newPerson = Person.builder()
                        .personId(personIdTextField.getText())
                        .firstName(StringUtils.capitalize(firstNameTextField.getText().trim()))
                        .lastName(StringUtils.capitalize(lastNameTextField.getText().trim()))
                        .registryNumber(RegistryNumber.fromText(registryNumberTextField.getText().trim()))
                        .gender(genderCombo.getValue())
                        .unitNumber(unitNumber)
                        .build();

                final LocalDate dob = dobDatePicker.getValue();
                if (dob != null) {
                    newPerson.addOrUpdateStatusChange(PersonStatusChange.EventType.BORN, dob);
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

    @Override
    protected void validateSaveButton(Node saveButton) {
        final boolean disable = personIdTextField.getText().trim().isEmpty()
                || firstNameTextField.getText().trim().isEmpty()
                || lastNameTextField.getText().trim().isEmpty();

        saveButton.setDisable(disable);
    }
}
