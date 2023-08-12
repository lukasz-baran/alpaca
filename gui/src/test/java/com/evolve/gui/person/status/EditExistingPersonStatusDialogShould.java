package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.gui.DialogTestBase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import java.time.LocalDate;

import static org.testfx.assertions.api.Assertions.assertThat;

public class EditExistingPersonStatusDialogShould extends DialogTestBase {
    private static final LocalDate DOB = LocalDate.of(1980, 8, 28);
    private static final PersonStatusChange DOB_PERSON_STATUS = new PersonStatusChange(
            PersonStatusChange.EventType.BORN, DOB, "foo");

    @Override
    protected EventHandler<ActionEvent> createTestedDialog() {
        return event -> {
            PersonStatusEditDialog personStatusEditDialog = new PersonStatusEditDialog(DOB_PERSON_STATUS,
                    new AcceptAnyValidator(), true);
            personStatusEditDialog.showDialog(new Stage());
        };
    }

    @Test
    void enableSaveButtonOnlyWhenRequiredControlsAreFilled(FxRobot robot) {
        // given
        robot.clickOn("#openDialog");
        FxRobot dialogWindow = robot.targetWindow("Status");

        // when -- dialog form is loaded
        final TextInputControl textOriginalValue = dialogWindow.lookup("#originalValue").queryTextInputControl();
        final DatePicker whenDatePicker = dialogWindow.lookup("#whenDatePicker").query();
        final ComboBox<PersonStatusChange.EventType> eventTypeCombo = dialogWindow.lookup("#eventTypeComboBox").query();

        // then -- the expected values are visible
        assertThat(textOriginalValue).hasText("foo");
        assertThat(whenDatePicker.getValue()).isEqualTo(DOB.toString());
        assertThat(eventTypeCombo.getValue()).isEqualTo(PersonStatusChange.EventType.BORN);

        final Button buttonSave = robot.targetWindow("Status").lookup("#saveButton").queryButton();
        assertThat(buttonSave).isDisabled().hasText("Zapisz");

        // when -- original value is filled in
        textOriginalValue.setText("test");

        // then -- save button is disabled
        assertThat(buttonSave).isEnabled();

        // when -- when date is selected
        whenDatePicker.setValue(LocalDate.now());

        // then -- save button is still disabled
        assertThat(buttonSave).isEnabled();

        // when -- event type is changed
        Platform.runLater(() -> eventTypeCombo.setValue(PersonStatusChange.EventType.JOINED));

        // then -- save button is enabled
        assertThat(buttonSave).isEnabled();
    }

}
