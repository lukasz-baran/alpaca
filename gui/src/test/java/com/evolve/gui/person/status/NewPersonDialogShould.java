package com.evolve.gui.person.status;

import com.evolve.domain.PersonStatusChange;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import java.time.LocalDate;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class NewPersonDialogShould {

    @BeforeEach
    public void setup() throws Exception {
        FxToolkit.setupSceneRoot(() -> {
            Button openDialogButton = new Button("Open Dialog");
            openDialogButton.setId("openDialog");
            openDialogButton.setOnAction(event -> {
                PersonStatusEditDialog personStatusEditDialog = new PersonStatusEditDialog(null, true);
                personStatusEditDialog.showDialog(new Stage());
            });
            StackPane root = new StackPane(openDialogButton);
            root.setPrefSize(500, 500);
            return new StackPane(root);
        });
        FxToolkit.setupStage(Stage::show);
    }

    @Test
    void makeSaveButtonEa(FxRobot robot) {
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
