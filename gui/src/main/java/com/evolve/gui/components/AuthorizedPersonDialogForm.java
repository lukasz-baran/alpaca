package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthorizedPersonDialogForm extends DialogWindow<Person.AuthorizedPerson> {

    private final Person.AuthorizedPerson authorizedPerson;

    @Override
    public Optional<Person.AuthorizedPerson> showDialog(Window window) {
        dialog.setTitle("Osoba upoważniona");
        dialog.initOwner(window);
        dialog.setHeaderText("Wprowadź dane osoby upoważnionej");

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        final ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

//        private String firstName;
//        private String lastName;
//        private String relation; // żona, mąż, syn, matka, córka, synowie
//        private String phone;
//        private Address address;
//        private String comment;

        final TextField firstNameTextField = new TextField();
        firstNameTextField.setPromptText("Imię");
        final TextField lastNameTextField = new TextField();
        lastNameTextField.setPromptText("Nazwisko");
        final TextField relationTextField = new TextField();
        relationTextField.setPromptText("Relacja");

        final TextArea commentTextArea = new TextArea();
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

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Person.AuthorizedPerson(
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        relationTextField.getText(),
                        null, null,
                        commentTextArea.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
