package com.evolve.gui.person.address;

import com.evolve.domain.Person;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public class PersonAddressDialog extends DialogWindow<Person.PersonAddress> {

    private final Person.PersonAddress personAddress;
    private final TextField streetTextField = new TextField();
    private final TextField postalCodeTextField = new TextField();
    private final TextField cityTextField = new TextField();
    private final ComboBox<Person.AddressType> typeComboBox;

    private final ObjectProperty<Person.AddressType> addressTypeObjectProperty = new SimpleObjectProperty<>();

    public PersonAddressDialog(Person.PersonAddress personAddress) {
        super("Adres", "Wprowadź adres");
        this.personAddress = personAddress;
        this.typeComboBox = new ComboBox<>();
        this.typeComboBox.getItems().addAll(Person.AddressType.values());
        this.typeComboBox.getSelectionModel().select(null);
        this.typeComboBox.valueProperty().bindBidirectional(addressTypeObjectProperty);
    }

    @Override
    public Optional<Person.PersonAddress> showDialog(Window window) {
        final Dialog<Person.PersonAddress> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        streetTextField.setPromptText("Ulica");
        postalCodeTextField.setPromptText("Kod pocztowy");
        cityTextField.setPromptText("Miasto");

        Optional.ofNullable(personAddress).ifPresent(address -> {
            streetTextField.setText(address.getStreet());
            postalCodeTextField.setText(address.getPostalCode());
            cityTextField.setText(address.getCity());
            addressTypeObjectProperty.setValue(personAddress.getType());
        });

        grid.add(new Label("Ulica:"), 0, 0);
        grid.add(streetTextField, 1, 0);
        grid.add(new Label("Kod pocztowy:"), 0, 1);
        grid.add(postalCodeTextField, 1, 1);
        grid.add(new Label("Miasto:"), 0, 2);
        grid.add(cityTextField, 1, 2);
        grid.add(new Label("Typ:"), 0, 3);
        grid.add(typeComboBox, 1, 3);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        streetTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        postalCodeTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        cityTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        addressTypeObjectProperty.addListener(change -> validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(streetTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return getPersonAddress();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(getPersonAddress().equals(this.personAddress));
    }

    private Person.PersonAddress getPersonAddress() {
        return new Person.PersonAddress(
                streetTextField.getText().trim(),
                postalCodeTextField.getText().trim(),
                cityTextField.getText().trim(),
                addressTypeObjectProperty.getValue());
    }
}
