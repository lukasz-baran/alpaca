package com.evolve.alpaca.gui.units;

import com.evolve.alpaca.unit.Unit;
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

public class UnitDialog extends DialogWindow<Unit> {
    private final List<UnitEntry> units;
    private final UnitEntry unitEntry;
    private final ComboBox<String> unitIdComboBox = new ComboBox<>();
    private final TextField unitNameTextField = new TextField();

    /**
     *
     * @param units already existing units in the system
     * @param unitEntry new or edited unit
     */
    public UnitDialog(List<UnitEntry> units, UnitEntry unitEntry) {
        super(isInEdition(unitEntry) ? "Edycja jednostki: " + unitEntry.getUnitNumber() : "Nowa jednostka" ,
            isInEdition(unitEntry) ? "Edytuj dane jednostki. Numer ID jest tylko do odczytu" :
                "Dodaj nową jednostkę. Numer ID zostanie wygenerowany automatycznie");
        this.units = units;
        this.unitEntry = unitEntry;
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

        unitIdComboBox.getItems().addAll(unitIds);

        unitNameTextField.setPromptText("Nazwa jednostki");
        unitNameTextField.setPrefWidth(300);

        grid.add(new Label("Numer:"), 0, 0);
        grid.add(unitIdComboBox, 1, 0);
        grid.add(new Label("Nazwa:"), 0, 1);
        grid.add(unitNameTextField, 1, 1);

        if (isInEdition(this.unitEntry)) {
            unitIdComboBox.getSelectionModel().select(this.unitEntry.getUnitNumber());
            unitIdComboBox.setEditable(false);
            unitIdComboBox.setDisable(true);
            unitNameTextField.setText(this.unitEntry.getUnitDescription());
        }

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(unitIdComboBox::requestFocus);

        unitIdComboBox.selectionModelProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        unitNameTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));

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

    @Override
    protected void validateSaveButton(Node saveButton) {

        final String description = unitNameTextField.getText().trim();
        boolean disable = unitIdComboBox.getValue().isEmpty() || description.isEmpty();

        if (isInEdition(this.unitEntry) && StringUtils.isNotEmpty(description)) {
            disable = StringUtils.equals(this.unitEntry.getUnitDescription(), description);
        }

        saveButton.setDisable(disable);
    }

    static boolean isInEdition(UnitEntry unitEntry) {
        return unitEntry != null;
    }

}
