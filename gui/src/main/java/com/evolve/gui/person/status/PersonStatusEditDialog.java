package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.components.SecureLocalDateStringConverter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PersonStatusEditDialog extends DialogWindow<PersonStatusChange> {
    public static final String ORIGINAL_VALUE_TEXT_ID = "originalValue";
    public static final String WHEN_DATE_PICKER_ID = "whenDatePicker";
    public static final String EVENT_TYPE_COMBO_BOX_ID = "eventTypeComboBox";
    private final PersonStatusChange editedValue;
    private final boolean isNewStatus;
    @Getter
    Dialog<PersonStatusChange> dialog;

    public static PersonStatusEditDialog newStatus() {
        return new PersonStatusEditDialog(null);
    }

    public PersonStatusEditDialog(PersonStatusChange editedValue) {
        this(editedValue, false);
    }

    PersonStatusEditDialog(PersonStatusChange editedValue, boolean isTestMode) {
        super("Status", createHeader(editedValue), isTestMode);
        this.editedValue = editedValue;
        this.isNewStatus = editedValue == null;
    }

    @Override
    public Optional<PersonStatusChange> showDialog(Window window) {
        this.dialog = createDialog(window);

        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        final ComboBox<PersonStatusChange.EventType> eventTypeComboBox = new ComboBox<>();
        eventTypeComboBox.setId(EVENT_TYPE_COMBO_BOX_ID);
        eventTypeComboBox.getItems().addAll(PersonStatusChange.EventType.values());

        final SecureLocalDateStringConverter whenConverter = new SecureLocalDateStringConverter();
        final DatePicker whenDatePicker = new DatePicker();
        whenDatePicker.setId(WHEN_DATE_PICKER_ID);
        whenDatePicker.setConverter(whenConverter);
        whenDatePicker.setPromptText("Data");

        final TextField originalValueTextField = new TextField();
        originalValueTextField.setId(ORIGINAL_VALUE_TEXT_ID);
        originalValueTextField.setPromptText("Oryginalna wartość");

        Optional.ofNullable(editedValue).ifPresent(status -> {
            eventTypeComboBox.setValue(status.getEventType());
            //eventTypeComboBox.setDisable(true);
            whenDatePicker.setValue(status.getWhen());
            originalValueTextField.setText(status.getOriginalValue());
        });

        grid.add(new Label("Typ zdarzenia:"), 0, 0);
        grid.add(eventTypeComboBox, 1, 0);
        grid.add(new Label("Data (DD.MM.YYYY):"), 0, 1);
        grid.add(whenDatePicker, 1, 1);
        grid.add(new Label("Oryginalna wartość/komentarz:"), 0, 2);
        grid.add(originalValueTextField, 1, 2);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        eventTypeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });

        whenDatePicker.valueProperty().addListener((options, oldValue, newValue) -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });

        originalValueTextField.textProperty().addListener(value -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(eventTypeComboBox::requestFocus);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {

                final PersonStatusChange actualEntry = new PersonStatusChange(eventTypeComboBox.getValue(),
                        whenDatePicker.getValue(), originalValueTextField.getText());

                log.info("actualEntry: {}", actualEntry);
                log.info("personHistoryStatusEntry: {}", editedValue);

                if (!actualEntry.equals(editedValue)) {
                    return actualEntry;
                }
            }
            return null;
        });

        if (isTestMode) {
            dialog.show();
            return Optional.empty();
        }
        return dialog.showAndWait();
    }

    void setButtonsState(ComboBox<PersonStatusChange.EventType> eventTypeComboBox,
            DatePicker whenDatePicker,
            TextField originalValueTextField) {
        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);

        // we should check whether we edit the value or add new one
        if (isNewStatus) {
            saveButton.setDisable(eventTypeComboBox.getValue() == null || whenDatePicker.getValue() == null);
        } else {
            final PersonStatusChange actualEntry = new PersonStatusChange(eventTypeComboBox.getValue(),
                    whenDatePicker.getValue(), originalValueTextField.getText());
            saveButton.setDisable(actualEntry.equals(editedValue));
        }

    }

    static String createHeader(PersonStatusChange personHistoryStatusEntry) {
        return Optional.ofNullable(personHistoryStatusEntry)
                .map(anything -> "Edytuj status")
                .orElse("Dodaj nowy status");
    }
}
