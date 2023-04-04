package com.evolve.gui.components;

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

public class PhoneNumberDialog extends DialogWindow<String> {
    private final String phoneNumber;

    public PhoneNumberDialog(String phoneNumber) {
        super("Telefon", "Podaj numer telefonu");
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Optional<String> showDialog(Window window) {
        final Dialog<String> dialog = createDialog(window);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final TextField phoneNumberTextField = new TextField();
        phoneNumberTextField.setPromptText("Numer telefonu");

        Optional.ofNullable(phoneNumber).ifPresent(phoneNumberTextField::setText);

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(phoneNumberTextField, 1, 0);

        // Enable/Disable login button depending on whether a username was entered.
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        phoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(phoneNumberTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return phoneNumberTextField.getText();
            }
            return null;
        });

        return dialog.showAndWait();

    }
}
