package com.evolve.alpaca.gui.export;

import com.evolve.alpaca.export.ExportTargetFormat;
import com.evolve.alpaca.export.PersonExportCriteria;
import com.evolve.alpaca.export.PersonExportType;
import com.evolve.gui.DialogWindow;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Optional;

@Slf4j
public class PersonExportDialog extends DialogWindow<PersonExportCriteria> {

    private final ComboBox<PersonExportType> exportTypeComboBox = new ComboBox<>(
            FXCollections.observableArrayList(PersonExportType.values()));
    private final RadioButton rb1 = new RadioButton("CSV (Comma separated values)");
    private final RadioButton rb2 = new RadioButton("JSON");
    private final RadioButton rb3 = new RadioButton("ODS (OpenOffice Sheets)");

    public PersonExportDialog() {
        super("Export danych", "Pozwala eksportować dane do CSV");
    }

    @Override
    public Optional<PersonExportCriteria> showDialog(Window window) {
        final Dialog<PersonExportCriteria> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final Button saveButton = findSubmitButton(dialog);
        saveButton.setText("Eksportuj");
        saveButton.setDisable(true);

        grid.add(new Label("Rodzaj eksportu:"), 0, 0);
        grid.add(exportTypeComboBox, 1, 0);

        final ToggleGroup toggleGroup = createExportTargetRadioButtons(grid);

        exportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        exportTypeComboBox.setValue(PersonExportType.ALL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                final ExportTargetFormat exportTargetFormat =
                        toggleGroup.getSelectedToggle().getUserData() instanceof ExportTargetFormat ?
                            (ExportTargetFormat) toggleGroup.getSelectedToggle().getUserData() :
                            ExportTargetFormat.CSV;

                return new PersonExportCriteria(exportTypeComboBox.getValue(), exportTargetFormat);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private ToggleGroup createExportTargetRadioButtons(final GridPane grid) {
        final ToggleGroup group = new ToggleGroup();
        rb1.setToggleGroup(group);
        rb1.setUserData(ExportTargetFormat.CSV);
        rb1.setSelected(true);

        rb2.setToggleGroup(group);
        rb2.setUserData(ExportTargetFormat.JSON);

        rb3.setToggleGroup(group);
        rb3.setUserData(ExportTargetFormat.ODS);
        rb3.setDisable(true);
        rb3.setTooltip(new Tooltip("Eksport do ODS nie jest jeszcze zaimplementowany"));

        HBox box = new HBox(20, rb1, rb2, rb3);
        grid.add(new Label("Eksportuj do:"), 0, 2);
        grid.add(box, 1, 2);
        return group;
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(exportTypeComboBox.getValue() == null);
    }


}
