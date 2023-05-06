package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public class NewPersonDialog extends DialogWindow<Person> {

    private final Person person;

    public NewPersonDialog() {
        super("Nowa osoba", "Podaj podstawowe dane osobowe nowego członka. Numer ID zostanie wygenerowany automatycznie");
        person = new Person();
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

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(firstNameTextField, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(lastNameTextField, 1, 1);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateSaveButton(saveButton, firstNameTextField, lastNameTextField);
        });
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateSaveButton(saveButton, firstNameTextField, lastNameTextField);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(firstNameTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return Person.builder()
                        .firstName(firstNameTextField.getText())
                        .lastName(lastNameTextField.getText())
                        .build();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    void validateSaveButton(
            Node saveButton, TextField firstNameTextField, TextField lastNameTextField) {
        boolean disable = firstNameTextField.getText().trim().isEmpty() || lastNameTextField.getText().trim().isEmpty();
        saveButton.setDisable(disable);
    }

}
