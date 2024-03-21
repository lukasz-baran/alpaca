package com.evolve.gui.person.contactDetails;

import com.evolve.domain.PersonContactData;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Optional;

public class PersonContactDetailsDialog extends DialogWindow<PersonContactData> {
    private final PersonContactData contactDetails;
    private final TextField dataTextField = new TextField();
    private final ComboBox<PersonContactData.ContactType> typeComboBox;
    private final ObjectProperty<PersonContactData.ContactType> contactTypeObjectProperty = new SimpleObjectProperty<>();
    private final TextField descriptionTextField = new TextField();

    public PersonContactDetailsDialog(PersonContactData contactData) {
        super("Dane kontaktowe",
                contactData != null ? "Edycja danych kontaktowych" : "Dodaj nowy numer telefon lub email");
        this.contactDetails = contactData;
        this.typeComboBox = new ComboBox<>();
        this.typeComboBox.getItems().addAll(PersonContactData.ContactType.values());
        this.typeComboBox.getSelectionModel().select(null);
        this.typeComboBox.valueProperty().bindBidirectional(contactTypeObjectProperty);
    }

    public static PersonContactDetailsDialog addNewPhoneOrEmail() {
        return new PersonContactDetailsDialog(null);
    }

    @Override
    public Optional<PersonContactData> showDialog(Window window) {
        final Dialog<PersonContactData> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        dataTextField.setPromptText("Telefon/email");

        Optional.ofNullable(contactDetails).ifPresent(data -> {
            dataTextField.setText(data.getData());
            contactTypeObjectProperty.setValue(data.getType());
            descriptionTextField.setText(data.getComment());
        });

        grid.add(new Label("Numer:"), 0, 0);
        grid.add(dataTextField, 1, 0);
        grid.add(new Label("Typ:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Komentarz:"), 0, 2);
        grid.add(descriptionTextField, 1, 2);

        attachValidators(dialog);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(dataTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new PersonContactData(StringUtils.trimToNull(StringUtils.trimToNull(dataTextField.getText())),
                        this.contactTypeObjectProperty.get(),
                        StringUtils.trimToNull(this.descriptionTextField.getText()));
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void attachValidators(final Dialog<PersonContactData> dialog) {
        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        validator.createCheck()
                .dependsOn("phoneOrEmail", dataTextField.textProperty())
                .withMethod(c -> {
                    final String phoneOrEmail = c.get("phoneOrEmail");
                    if (StringUtils.isBlank(phoneOrEmail)) {
                        c.error("Nie wprowadzono wartości");
                        return;
                    }

                    if (typeComboBox.getValue() == PersonContactData.ContactType.EMAIL && !EmailValidator.getInstance().isValid(phoneOrEmail)) {
                        c.error("Podany adres email jest niepoprawny");
                    }
                })
                .decorates(dataTextField)
                .immediate();
        validator.createCheck()
                .dependsOn("type", contactTypeObjectProperty)
                .withMethod(c -> {
                    final PersonContactData.ContactType type = c.get("type");
                    if (type == null) {
                        c.error("Typ nie został wybrany");
                    }
                })
                .decorates(typeComboBox)
                .immediate();

        dataTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        contactTypeObjectProperty.addListener(change -> validateSaveButton(saveButton));
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        final String newDataText = StringUtils.trimToNull(this.dataTextField.getText());
        final PersonContactData.ContactType newType = this.contactTypeObjectProperty.get();
        final String newComment = StringUtils.trimToNull(this.descriptionTextField.getText());

        final boolean canBeSaved = validator.validate() && !new PersonContactData(newDataText, newType, newComment)
                .equals(this.contactDetails);

        saveButton.setDisable(!canBeSaved);

    }

}
