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
public class PersonAddressDialog extends DialogWindow<Person.PersonAddress> {

    private final Person.PersonAddress personAddress;

    @Override
    public Optional<Person.PersonAddress> showDialog(Window window) {
        //final Dialog<Person.PersonAddress> dialog = new Dialog<>();
        dialog.setTitle("Adres");
        dialog.initOwner(window);
        dialog.setHeaderText("Wprowadź adres");

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        final ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField streetTextField = new TextField();
        streetTextField.setPromptText("Ulica");
        final TextField postalCodeTextField = new TextField();
        postalCodeTextField.setPromptText("Kod pocztowy");
        final TextField cityTextField = new TextField();
        cityTextField.setPromptText("Miasto");

        Optional.ofNullable(personAddress).ifPresent(address -> {
            streetTextField.setText(address.getStreet());
            postalCodeTextField.setText(address.getPostalCode());
            cityTextField.setText(address.getCity());
        });

        grid.add(new Label("Ulica:"), 0, 0);
        grid.add(streetTextField, 1, 0);
        grid.add(new Label("Kod pocztowy:"), 0, 1);
        grid.add(postalCodeTextField, 1, 1);
        grid.add(new Label("Miasto:"), 0, 2);
        grid.add(cityTextField, 1, 2);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        streetTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(streetTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Person.PersonAddress(
                        streetTextField.getText(),
                        postalCodeTextField.getText(),
                        cityTextField.getText(),
                        Person.AddressType.HOME);
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
