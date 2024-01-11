package com.evolve.gui.person.status;

import com.evolve.alpaca.util.DatePickerKeyEventHandler;
import com.evolve.alpaca.util.LocalDateStringConverter;
import com.evolve.alpaca.validation.ValidationResult;
import com.evolve.alpaca.validation.Validator;
import com.evolve.domain.PersonStatusChange;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class PersonStatusEditDialog extends DialogWindow<PersonStatusChange> {
    public static final String ORIGINAL_VALUE_TEXT_ID = "originalValue";
    public static final String WHEN_DATE_PICKER_ID = "whenDatePicker";
    public static final String EVENT_TYPE_COMBO_BOX_ID = "eventTypeComboBox";
    private final PersonStatusChange editedValue;
    private final boolean isNewStatus;
    private final Validator<PersonStatusChange> addingNewStatusValidator;
    private final ComboBox<PersonStatusChange.EventType> eventTypeComboBox;
    private final TextField originalValueTextField;
    private final DatePicker whenDatePicker;

    @Getter
    Dialog<PersonStatusChange> dialog;

    public static PersonStatusEditDialog newStatus(List<PersonStatusChange> existingStatuses) {
        return new PersonStatusEditDialog(null, new AddingNewStatusValidator(existingStatuses), false);
    }

    public static PersonStatusEditDialog editStatus(PersonStatusChange editedValue) {
        return new PersonStatusEditDialog(editedValue, new AcceptAnyValidator(), false);
    }

    PersonStatusEditDialog(PersonStatusChange editedValue,
            Validator<PersonStatusChange> validator,
            boolean isTestMode) {
        super("Status", createHeader(editedValue), isTestMode);
        this.editedValue = editedValue;
        this.addingNewStatusValidator = validator;
        this.isNewStatus = editedValue == null;
        this.eventTypeComboBox = new ComboBox<>();
        this.originalValueTextField = new TextField();
        this.whenDatePicker = new DatePicker();
    }

    @Override
    public Optional<PersonStatusChange> showDialog(Window window) {
        this.dialog = createDialog(window);

        final GridPane grid = createGridPane();

        eventTypeComboBox.setId(EVENT_TYPE_COMBO_BOX_ID);
        eventTypeComboBox.getItems().addAll(PersonStatusChange.EventType.values());

        final LocalDateStringConverter whenConverter = new LocalDateStringConverter();
        whenDatePicker.setId(WHEN_DATE_PICKER_ID);
        whenDatePicker.setConverter(whenConverter);
        whenDatePicker.setPromptText("Data");
        whenDatePicker.getEditor().setOnKeyTyped(new DatePickerKeyEventHandler(whenConverter, whenDatePicker));

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

        registerEventHandlers(window, saveButton);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(eventTypeComboBox::requestFocus);

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

    void registerEventHandlers(Window window, Node saveButton) {
        saveButton.addEventFilter(ActionEvent.ACTION,
            new SaveButtonEventHandler(eventTypeComboBox, whenDatePicker, originalValueTextField, addingNewStatusValidator, window));

        eventTypeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });

        whenDatePicker.valueProperty().addListener((options, oldValue, newValue) -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });

        originalValueTextField.textProperty().addListener(value -> {
            setButtonsState(eventTypeComboBox, whenDatePicker, originalValueTextField);
        });
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

    @RequiredArgsConstructor
    public static class SaveButtonEventHandler implements EventHandler<ActionEvent> {
        private final ComboBox<PersonStatusChange.EventType> eventTypeComboBox;
        private final DatePicker whenDatePicker;
        private final TextField originalValueTextField;
        private final Validator<PersonStatusChange> addingNewStatusValidator;
        private final Window window;

        @Override
        public void handle(ActionEvent event) {
            final PersonStatusChange actualEntry = new PersonStatusChange(eventTypeComboBox.getValue(),
                    whenDatePicker.getValue(), originalValueTextField.getText());

            final ValidationResult validationResult = addingNewStatusValidator.validate(actualEntry);
            if (!validationResult.isValid()) {
                StageManager.displayInformation(window, validationResult
                        .getErrors().stream().findFirst().orElse("Nieznany błąd walidacji - nie można zapisać statusu"));
                // we should consume the event to prevent closing the dialog
                event.consume();
            }
        }
    }

}


