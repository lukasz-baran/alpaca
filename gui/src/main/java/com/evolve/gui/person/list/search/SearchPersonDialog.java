package com.evolve.gui.person.list.search;

import com.evolve.domain.Unit;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.person.UnitNumberItem;
import com.evolve.services.UnitsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        unitNumberCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                saveButton.setDisable(false));

        dialog.getDialogPane().setContent(grid);

//        Platform.runLater(phoneNumberTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                final String unitNumber = Optional.ofNullable(unitNumberCombo.getValue())
                        .filter(unitNumberItem -> unitNumberItem != UnitNumberItem.ALL)
                        .map(UnitNumberItem::unitNumber)
                        .orElse(null);

                return new PersonSearchCriteria(unitNumber);
            }
            return null;
        });

        return dialog.showAndWait();

    }


}
