package com.evolve.gui.person.address;

import com.evolve.domain.Person;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public class PersonAddressDialog extends DialogWindow<Person.PersonAddress> {

    private final Person.PersonAddress personAddress;

    public PersonAddressDialog(Person.PersonAddress personAddress) {
        super("Adres", "Wprowadź adres");
        this.personAddress = personAddress;
    }

    @Override
    public Optional<Person.PersonAddress> showDialog(Window window) {
        final Dialog<Person.PersonAddress> dialog = createDialog(window);

        final GridPane grid = createGridPane();

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
