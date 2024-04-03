package com.evolve.gui.person.list.search;

import com.evolve.alpaca.unit.services.UnitsService;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.gui.person.UnitNumberItem;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.Optional;

public class PersonCriteriaGrid {

    private final ComboBox<UnitNumberItem> unitNumberCombo;
    private final CheckBox hasDocumentsCheckBox = new CheckBox();
    private final ComboBox<GenderSearchItem> personGenderCombo;
    private final ComboBox<PersonStatusSearchItem> personStatusCombo;
    private final CheckBox retiredCheckBox = new CheckBox();
    private final CheckBox exemptFromFeesCheckBox = new CheckBox();

    public PersonCriteriaGrid(UnitsService unitsService) {
        final ObservableList<UnitNumberItem> units = FXCollections.observableArrayList();
        units.add(UnitNumberItem.ALL);
        units.addAll(unitsService.fetchList().stream()
                .map(unit -> new UnitNumberItem(unit.getId(), unit.getName()))
                .toList());
        this.unitNumberCombo = new ComboBox<>(units);

        this.personStatusCombo = new ComboBox<>(PersonStatusSearchItem.getSearchItems());
        this.personGenderCombo = new ComboBox<>(GenderSearchItem.getSearchItems());
    }

    GridPane createPersonSearchCriteria(GridPane gridPersonCriteria, ValidatorHandler saveHandler) {

        gridPersonCriteria.add(new Label("Dane osób:"), 0, 0, 2, 1);

        gridPersonCriteria.add(new Label("Jednostka:"), 0, 1);
        gridPersonCriteria.add(unitNumberCombo, 1, 1);

        hasDocumentsCheckBox.indeterminateProperty().set(true);
        hasDocumentsCheckBox.setAllowIndeterminate(true);

        gridPersonCriteria.add(new Label("Załączniki:"), 0, 2);
        gridPersonCriteria.add(hasDocumentsCheckBox, 1, 2);

        gridPersonCriteria.add(new Label("Status:"), 0, 3);
        gridPersonCriteria.add(personStatusCombo, 1, 3);

        gridPersonCriteria.add(new Label("Płeć:"), 0, 4);
        gridPersonCriteria.add(personGenderCombo, 1, 4);

        retiredCheckBox.indeterminateProperty().set(true);
        retiredCheckBox.setAllowIndeterminate(true);

        gridPersonCriteria.add(new Label("Emeryt:"), 0, 5);
        gridPersonCriteria.add(retiredCheckBox, 1, 5);

        exemptFromFeesCheckBox.indeterminateProperty().set(true);
        exemptFromFeesCheckBox.setAllowIndeterminate(true);

        gridPersonCriteria.add(new Label("Zwolniony:"), 0, 6);
        gridPersonCriteria.add(exemptFromFeesCheckBox, 1, 6);


        final ChangeListener<Boolean> attachmentsListener = (prop, old, val) -> {
            updateLabelOnAttachments(hasDocumentsCheckBox);
            saveHandler.validate();
        };
        hasDocumentsCheckBox.selectedProperty().addListener(attachmentsListener);
        hasDocumentsCheckBox.indeterminateProperty().addListener(attachmentsListener);
        final ChangeListener<Boolean> retiredListener = (prop, old, val) -> {
            updateLabelOnRetired(retiredCheckBox);
            saveHandler.validate();
        };
        retiredCheckBox.selectedProperty().addListener(retiredListener);
        retiredCheckBox.indeterminateProperty().addListener(retiredListener);
        final ChangeListener<Boolean> exemptFromFeesListener = (prop, old, val) -> {
            updateLabelOnExemptFromFees(exemptFromFeesCheckBox);
            saveHandler.validate();
        };
        exemptFromFeesCheckBox.selectedProperty().addListener(exemptFromFeesListener);
        exemptFromFeesCheckBox.indeterminateProperty().addListener(exemptFromFeesListener);

        unitNumberCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                saveHandler.validate());

        personStatusCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                saveHandler.validate());

        personGenderCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                saveHandler.validate());

        return gridPersonCriteria;
    }

    String getUnitNumber() {
        return Optional.ofNullable(unitNumberCombo.getValue())
                .filter(unitNumberItem -> unitNumberItem != UnitNumberItem.ALL)
                .map(UnitNumberItem::unitNumber)
                .orElse(null);
    }

    Boolean hasDocuments() {
        return hasDocumentsCheckBox.isIndeterminate() ? null : hasDocumentsCheckBox.isSelected();
    }

    Boolean isRetired() {
        return retiredCheckBox.isIndeterminate() ? null : retiredCheckBox.isSelected();
    }

    Boolean isExemptFromFees() {
        return exemptFromFeesCheckBox.isIndeterminate() ? null : exemptFromFeesCheckBox.isSelected();
    }

    PersonStatus getPersonStatus()  {
        return Optional.ofNullable(personStatusCombo.getValue())
                .map(PersonStatusSearchItem::personStatus)
                .orElse(null);
    }

    Person.Gender getPersonGender() {
        return Optional.ofNullable(personGenderCombo.getValue())
                .map(GenderSearchItem::gender)
                .orElse(null);
    }

    private static void updateLabelOnAttachments(CheckBox hasDocumentsCheckBox) {
        final String txt = hasDocumentsCheckBox.isIndeterminate() ? "Bez znaczenia" :
                hasDocumentsCheckBox.isSelected() ? "Obecne" :
                        "Brak załączników";
        hasDocumentsCheckBox.setText(txt);
    }

    private static void updateLabelOnRetired(CheckBox retiredCheckBox) {
        final String txt = retiredCheckBox.isIndeterminate() ? "Bez znaczenia" :
                retiredCheckBox.isSelected() ? "Jest" :
                        "Nie jest";
        retiredCheckBox.setText(txt);
    }

    private static void updateLabelOnExemptFromFees(CheckBox exemptFromFeesCheckBox) {
        final String txt = exemptFromFeesCheckBox.isIndeterminate() ? "Bez znaczenia" :
                exemptFromFeesCheckBox.isSelected() ? "Jest" :
                        "Nie jest";
        exemptFromFeesCheckBox.setText(txt);
    }

    boolean isAnyPersonFilterChanged() {
        return Objects.nonNull(unitNumberCombo.getValue()) ||
                !hasDocumentsCheckBox.isIndeterminate() ||
                Objects.nonNull(personStatusCombo.getValue()) ||
                Objects.nonNull(personGenderCombo.getValue()) ||
                !retiredCheckBox.isIndeterminate() ||
                !exemptFromFeesCheckBox.isIndeterminate();
    }

}
