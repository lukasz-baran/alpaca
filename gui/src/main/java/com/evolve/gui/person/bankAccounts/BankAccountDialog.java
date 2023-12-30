package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public class BankAccountDialog extends DialogWindow<BankAccountEntry>  {

    private final BankAccountEntry bankAccountEntry;

    public BankAccountDialog(BankAccountEntry bankAccount) {
        super("Konto bankowe", "Wprowadź numer konta bankowego");
        this.bankAccountEntry = bankAccount;
    }

    @Override
    public Optional<BankAccountEntry> showDialog(Window window) {
        final Dialog<BankAccountEntry> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final TextField bankAccountNumberTextField = new TextField();
        bankAccountNumberTextField.setPromptText("Numer konta");
        bankAccountNumberTextField.textProperty().addListener(event -> {
                    bankAccountNumberTextField.setStyle("");
                    final String input = bankAccountNumberTextField.getText();

                    if (!input.isEmpty() && !BankAccount.isValid(input)) {
                        bankAccountNumberTextField.setStyle("-fx-border-color: red");
                    }
                });

        Optional.ofNullable(bankAccountEntry).ifPresent(bankAccountEntry -> {
            bankAccountNumberTextField.setText(bankAccountEntry.getNumber());
        });

        grid.add(new Label("Numer konta:"), 0, 0);
        grid.add(bankAccountNumberTextField, 1, 0);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        bankAccountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(bankAccountNumberTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new BankAccountEntry(
                        bankAccountNumberTextField.getText());
            }
            return null;
        });

        return dialog.showAndWait();

    }
}
