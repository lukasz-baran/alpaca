package com.evolve.alpaca.gui.export;

import com.evolve.alpaca.export.PersonExportCriteria;
import com.evolve.alpaca.export.PersonExportType;
import com.evolve.gui.DialogWindow;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PersonExportDialog extends DialogWindow<PersonExportCriteria> {

    private final ComboBox<PersonExportType> exportTypeComboBox = new ComboBox<>(
            FXCollections.observableArrayList(PersonExportType.values()));

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

//        grid.add(new Label("TODO TODO TODO:"), 0, 1);
//        grid.add(groupAccounts, 1, 1);

        exportTypeComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        exportTypeComboBox.setValue(PersonExportType.ALL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {

                return new PersonExportCriteria(exportTypeComboBox.getValue());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(exportTypeComboBox.getValue() == null);
    }


}
