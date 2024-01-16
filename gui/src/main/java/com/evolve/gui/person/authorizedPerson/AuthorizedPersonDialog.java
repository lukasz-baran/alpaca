package com.evolve.gui.person.authorizedPerson;

import com.evolve.domain.Person;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public class AuthorizedPersonDialog extends DialogWindow<Person.AuthorizedPerson> {

    private final Person.AuthorizedPerson authorizedPerson;
    private final TextField firstNameTextField = new TextField();
    private final TextField lastNameTextField = new TextField();
    private final TextField relationTextField = new TextField();
    private final TextArea commentTextArea = new TextArea();

    public AuthorizedPersonDialog(Person.AuthorizedPerson authorizedPerson) {
        super("Osoba upoważniona", "Wprowadź dane osoby upoważnionej");
        this.authorizedPerson = authorizedPerson;
    }

    @Override
    public Optional<Person.AuthorizedPerson> showDialog(Window window) {
        final Dialog<Person.AuthorizedPerson> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        firstNameTextField.setPromptText("Imię");
        lastNameTextField.setPromptText("Nazwisko");
        relationTextField.setPromptText("Relacja");
        commentTextArea.setPrefRowCount(3);
        commentTextArea.setPromptText("Komentarz");

        Optional.ofNullable(authorizedPerson).ifPresent(person -> {
            firstNameTextField.setText(person.getFirstName());
            lastNameTextField.setText(person.getLastName());
            relationTextField.setText(person.getRelation());
            commentTextArea.setText(person.getComment());
        });

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(firstNameTextField, 1, 0);
        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(lastNameTextField, 1, 1);
        grid.add(new Label("Relacja:"), 0, 2);
        grid.add(relationTextField, 1, 2);
        grid.add(new Label("Komentarz:"), 0, 3);
        grid.add(commentTextArea, 1, 3);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(saveButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(firstNameTextField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getAuthorizedPerson();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(getAuthorizedPerson().equals(this.authorizedPerson));
    }

    private Person.AuthorizedPerson getAuthorizedPerson() {
        return new Person.AuthorizedPerson(
                firstNameTextField.getText().trim(),
                lastNameTextField.getText().trim(),
                relationTextField.getText().trim(),
                null, null,
                commentTextArea.getText());
    }

}
