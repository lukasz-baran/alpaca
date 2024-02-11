package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import com.evolve.gui.DialogTestBase;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import java.time.LocalDate;

import static org.testfx.assertions.api.Assertions.assertThat;

@Disabled
public class NewPersonStatusDialogShould extends DialogTestBase {

    @Override
    protected EventHandler<ActionEvent> createTestedDialog() {
        return event -> {
            PersonStatusEditDialog personStatusEditDialog = new PersonStatusEditDialog(null, new AcceptAnyValidator(), true);
            personStatusEditDialog.showDialog(new Stage());
        };
    }

    @Test
    void enableSaveButtonOnlyWhenRequiredControlsAreFilled(FxRobot robot) {
        // given
        robot.clickOn("#openDialog");
        FxRobot dialogWindow = robot.targetWindow("Status");

        var textOriginalValue = dialogWindow.lookup("#originalValue").queryTextInputControl();
        final DatePicker whenDatePicker = dialogWindow.lookup("#whenDatePicker").query();
        final ComboBox<PersonStatusChange.EventType> eventTypeCombo = dialogWindow.lookup("#eventTypeComboBox").query();

        final Button buttonSave = robot.targetWindow("Status").lookup("#saveButton").queryButton();
        assertThat(buttonSave).isDisabled().hasText("Zapisz");

        // when -- original value is filled in
        textOriginalValue.setText("test");

        // then -- save button is disabled
        assertThat(buttonSave).isDisabled();

        // when -- when date is selected
        whenDatePicker.setValue(LocalDate.now());

        // then -- save button is still disabled
        assertThat(buttonSave).isDisabled();

        // when -- event type is selected
        Platform.runLater(() -> eventTypeCombo.setValue(PersonStatusChange.EventType.BORN));

        // then -- save button is enabled
        assertThat(buttonSave).isEnabled();
    }

}
