package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import com.evolve.gui.DialogWindow;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class BankAccountDialog extends DialogWindow<BankAccount>  {

    private final BankAccount bankAccountEntry;
    private final TextField bankAccountNumberTextField = new TextField();
    private final TextArea textAreaNotes = new TextArea();

    public BankAccountDialog(BankAccount bankAccount) {
        super("Konto bankowe", "Wprowadź numer konta bankowego");
        this.bankAccountEntry = bankAccount;
    }

    @Override
    public Optional<BankAccount> showDialog(Window window) {
        final Dialog<BankAccount> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        bankAccountNumberTextField.setMaxWidth(300);
        bankAccountNumberTextField.setPrefWidth(300);
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
            textAreaNotes.setText(bankAccountEntry.getNotes());
        });

        grid.add(new Label("Numer konta:"), 0, 0);
        grid.add(bankAccountNumberTextField, 1, 0);

        grid.add(new Label("Notatki:"), 0, 1);
        grid.add(textAreaNotes, 1, 1);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        validator.createCheck()
                .dependsOn("bankAccount", bankAccountNumberTextField.textProperty())
                .withMethod(c -> {
                    final String bankAccount = c.get("bankAccount");
                    if (StringUtils.isBlank(bankAccount)) {
                        c.error("Nie wprowadzono wartości");
                        return;
                    }
                    if (!BankAccount.isValid(bankAccount)) {
                        c.error("Podany numer konta jest niepoprawny (zły numer IBAN)");
                    }

                })
                .decorates(bankAccountNumberTextField)
                .immediate();
        bankAccountNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        textAreaNotes.textProperty().addListener((observableValue, s, t1) -> validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(bankAccountNumberTextField::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new BankAccount(
                        bankAccountNumberTextField.getText(), textAreaNotes.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        final String newBankNumber = StringUtils.trimToNull(this.bankAccountNumberTextField.getText());
        final String newNotes = Strings.emptyToNull(this.textAreaNotes.getText());

        final boolean canBeSaved = validator.validate() && !BankAccount.of(newBankNumber, newNotes).equals(this.bankAccountEntry);
        saveButton.setDisable(!canBeSaved);
    }

}
