package com.evolve.alpaca.gui.units;

import com.evolve.domain.Unit;
import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class NewUnitDialog extends DialogWindow<Unit> {
    private final List<UnitsController.UnitEntry> units;

    public NewUnitDialog(List<UnitsController.UnitEntry> units) {
        super("Nowa jednostka", "Dodaj nową jednostkę. Numer ID zostanie wygenerowany automatycznie");
        this.units = units;
    }

    @Override
    public Optional<Unit> showDialog(Window window) {
        final Dialog<Unit> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final List<String> unitIds = IntStream.range(1, 99)
                .boxed()
                .map(i -> String.format("%02d", i))
                .filter(i -> units.stream().noneMatch(unit -> StringUtils.equals(unit.getUnitNumber(), i)))
                .toList();

        final ComboBox<String> unitIdComboBox = new ComboBox<>();
        unitIdComboBox.getItems().addAll(unitIds);

        final TextField unitNameTextField = new TextField();
        unitNameTextField.setPromptText("Nazwa jednostki");

        grid.add(new Label("Numer:"), 0, 0);
        grid.add(unitIdComboBox, 1, 0);
        grid.add(new Label("Nazwa:"), 0, 1);
        grid.add(unitNameTextField, 1, 1);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(unitIdComboBox::requestFocus);

        unitIdComboBox.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            validateSaveButton(saveButton, unitIdComboBox, unitNameTextField);
        });
        unitNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateSaveButton(saveButton, unitIdComboBox, unitNameTextField);
        });


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Unit(
                        unitIdComboBox.getValue(),
                        StringUtils.capitalize(unitNameTextField.getText().trim()));
            }
            return null;
        });

        return dialog.showAndWait();
    }

    void validateSaveButton(
            Node saveButton, ComboBox<String> unitIdComboBox, TextField unitNameTextField) {
        final boolean disable = unitIdComboBox.getValue().isEmpty()
                || unitNameTextField.getText().trim().isEmpty();

        saveButton.setDisable(disable);
    }

}
