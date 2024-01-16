package com.evolve.gui.person.list.search;

import com.evolve.domain.Unit;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.person.UnitNumberItem;
import com.evolve.services.UnitsService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SearchPersonDialog extends DialogWindow<PersonSearchCriteria> {
    private final UnitsService unitsService;

    public SearchPersonDialog(UnitsService unitsService) {
        super("Szukaj osób", "Wprowadź kryteria wyszukiwania");
        this.unitsService = unitsService;
    }

    @Override
    public Optional<PersonSearchCriteria> showDialog(Window window) {
        final Dialog<PersonSearchCriteria> dialog = createDialog(window);
        findSubmitButton(dialog)
                .setText("Szukaj");

        final GridPane grid = createGridPane();

        final ObservableList<UnitNumberItem> units = FXCollections.observableArrayList();
        units.add(UnitNumberItem.ALL);
        for (Unit unit : unitsService.fetchList()) {
            units.add(new UnitNumberItem(unit.getId(), unit.getName()));
        }
        final ComboBox<UnitNumberItem> unitNumberCombo = new ComboBox<>(units);

        grid.add(new Label("Jednostka:"), 0, 0);
        grid.add(unitNumberCombo, 1, 0);

        final CheckBox hasDocumentsCheckBox = new CheckBox();
        hasDocumentsCheckBox.indeterminateProperty().set(true);
        hasDocumentsCheckBox.setAllowIndeterminate(true);

        grid.add(new Label("Załączniki:"), 0, 1);
        grid.add(hasDocumentsCheckBox, 1, 1);


        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        final ChangeListener<Boolean> listener = (prop, old, val) -> {
            updateLabelOnAttachments(hasDocumentsCheckBox, saveButton);
            validateSaveButton(saveButton);
        };
        hasDocumentsCheckBox.selectedProperty().addListener(listener);
        hasDocumentsCheckBox.indeterminateProperty().addListener(listener);

        unitNumberCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                final String unitNumber = Optional.ofNullable(unitNumberCombo.getValue())
                        .filter(unitNumberItem -> unitNumberItem != UnitNumberItem.ALL)
                        .map(UnitNumberItem::unitNumber)
                        .orElse(null);

                final Boolean hasDocuments = hasDocumentsCheckBox.isIndeterminate() ? null :
                                hasDocumentsCheckBox.isSelected();

                return new PersonSearchCriteria(unitNumber, hasDocuments);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(false);
    }

    private void updateLabelOnAttachments(CheckBox hasDocumentsCheckBox, Node saveButton) {
        final String txt = hasDocumentsCheckBox.isIndeterminate() ? "Bez znaczenia" :
                hasDocumentsCheckBox.isSelected() ? "Obecne" :
                        "Brak załączników";
        hasDocumentsCheckBox.setText(txt);
    }


}
