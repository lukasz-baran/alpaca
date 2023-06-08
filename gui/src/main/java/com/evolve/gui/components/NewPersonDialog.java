package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;
import com.evolve.gui.DialogWindow;
import com.evolve.services.PersonsService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Optional;

public class NewPersonDialog extends DialogWindow<Person> {

    private final PersonsService personsService;

    public NewPersonDialog(PersonsService personsService) {
        super("Nowa osoba", "Podaj podstawowe dane osobowe nowego członka. Numer ID zostanie wygenerowany automatycznie");
        this.personsService = personsService;
    }

    @Override
    public Optional<Person> showDialog(Window window) {
        final Dialog<Person> dialog = createDialog(window);

        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("Imię");
        final TextField lastNameTextField = new TextField();
        lastNameTextField.setPromptText("Nazwisko");

        final TextField personIdTextField = new TextField();
        personIdTextField.setPromptText("ID");
        personIdTextField.setEditable(false);

        final DatePicker joinedDateDatePicker = new DatePicker();
        joinedDateDatePicker.setPromptText("Data dołączenia");

        final DatePicker dobDatePicker = new DatePicker();
        dobDatePicker.setPromptText("Data urodzenia");

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(firstNameTextField, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(lastNameTextField, 1, 1);

        grid.add(new Label("ID:"), 0, 2);
        grid.add(personIdTextField, 1, 2);

        grid.add(new Label("Dołączył(a):"), 0, 3);
        grid.add(joinedDateDatePicker, 1, 3);

        grid.add(new Label("Data urodzenia:"), 0, 4);
        grid.add(dobDatePicker, 1, 4);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
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

                final Person newPerson = Person.builder()
                        .personId(personIdTextField.getText())
                        .firstName(StringUtils.capitalize(firstNameTextField.getText().trim()))
                        .lastName(StringUtils.capitalize(lastNameTextField.getText().trim()))
                        .build();

                final LocalDate dob = dobDatePicker.getValue();
                if (dob != null) {
                    newPerson.updatePersonDob(dob);
                }

                final LocalDate joined = joinedDateDatePicker.getValue();
                if (joined != null) {
                    newPerson.addOrUpdateStatusChange(PersonStatusChange.EventType.JOINED, joined);
                }

                return newPerson;
            }
            return null;
        });

        return dialog.showAndWait();
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

}
