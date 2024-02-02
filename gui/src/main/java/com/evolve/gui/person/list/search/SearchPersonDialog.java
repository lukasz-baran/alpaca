package com.evolve.gui.person.list.search;

import com.evolve.domain.Account;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
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
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class SearchPersonDialog extends DialogWindow<PersonSearchCriteria> {

    private final ComboBox<UnitNumberItem> unitNumberCombo;
    private final CheckBox hasDocumentsCheckBox = new CheckBox();
    private final ComboBox<GenderSearchItem> personGenderCombo;
    private final ComboBox<PersonStatusSearchItem> personStatusCombo;

    // accounts-related checkboxes:
    final CheckBox hasFeesAccountsCheckBox = new CheckBox();
    final CheckBox hasLoansAccountsCheckBox = new CheckBox();
    final CheckBox hasPaydayLoansAccountsCheckBox = new CheckBox();
    final CheckBox hasDeathBenefitsAccountsCheckBox = new CheckBox();
    final CheckBox hasExemptFromFeesAccountsCheckBox = new CheckBox();
    final CheckBox hasResignedAccountsCheckBox = new CheckBox();
    final CheckBox hasRemovedAccountsCheckBox = new CheckBox();
    final CheckBox hasDeceasedAccountsCheckBox = new CheckBox();


    public SearchPersonDialog(UnitsService unitsService) {
        super("Szukaj osób", "Wprowadź kryteria wyszukiwania");

        final ObservableList<UnitNumberItem> units = FXCollections.observableArrayList();
        units.add(UnitNumberItem.ALL);
        units.addAll(unitsService.fetchList().stream()
                .map(unit -> new UnitNumberItem(unit.getId(), unit.getName()))
                .toList());
        this.unitNumberCombo = new ComboBox<>(units);

        this.personStatusCombo = new ComboBox<>(PersonStatusSearchItem.getSearchItems());
        this.personGenderCombo = new ComboBox<>(GenderSearchItem.getSearchItems());
    }

    @Override
    public Optional<PersonSearchCriteria> showDialog(Window window) {
        final Dialog<PersonSearchCriteria> dialog = createDialog(window);
        findSubmitButton(dialog)
                .setText("Szukaj");
        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        final HBox mainHBox = new HBox();
        final GridPane gridPersonCriteria = createPersonSearchCriteria(saveButton);
        final GridPane gridAccountCriteria = createAccountsSearchCriteria(saveButton);
        mainHBox.getChildren().addAll(gridPersonCriteria, gridAccountCriteria);

        dialog.getDialogPane().setContent(mainHBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                final String unitNumber = Optional.ofNullable(unitNumberCombo.getValue())
                        .filter(unitNumberItem -> unitNumberItem != UnitNumberItem.ALL)
                        .map(UnitNumberItem::unitNumber)
                        .orElse(null);

                final Boolean hasDocuments = hasDocumentsCheckBox.isIndeterminate() ? null :
                                hasDocumentsCheckBox.isSelected();

                final PersonStatus personStatus = Optional.ofNullable(personStatusCombo.getValue())
                        .map(PersonStatusSearchItem::personStatus)
                        .orElse(null);

                final Person.Gender personGender = Optional.ofNullable(personGenderCombo.getValue())
                        .map(GenderSearchItem::gender)
                        .orElse(null);

                return new PersonSearchCriteria(unitNumber, hasDocuments, personStatus, personGender,
                        getSelectedAccountTypes(),
                        getSelectedAccountUnitNumbers());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private GridPane createPersonSearchCriteria(Node saveButton) {
        final GridPane gridPersonCriteria = createGridPane();

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

        final ChangeListener<Boolean> listener = (prop, old, val) -> {
            updateLabelOnAttachments(hasDocumentsCheckBox);
            validateSaveButton(saveButton);
        };
        hasDocumentsCheckBox.selectedProperty().addListener(listener);
        hasDocumentsCheckBox.indeterminateProperty().addListener(listener);

        unitNumberCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        personStatusCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        personGenderCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                validateSaveButton(saveButton));

        return gridPersonCriteria;
    }

    private GridPane createAccountsSearchCriteria(Node saveButton) {
        final GridPane gridAccountCriteria = createGridPane();
        gridAccountCriteria.add(new Label("Dane konta:"), 0, 0, 2, 1);

        gridAccountCriteria.add(new Label("200.. składki:"), 0, 1);
        gridAccountCriteria.add(hasFeesAccountsCheckBox, 1, 1);

        gridAccountCriteria.add(new Label("201.. pożyczki:"), 0, 2);
        gridAccountCriteria.add(hasLoansAccountsCheckBox, 1, 2);

        gridAccountCriteria.add(new Label("203.. chwilówki:"), 0, 3);
        gridAccountCriteria.add(hasPaydayLoansAccountsCheckBox, 1, 3);

        gridAccountCriteria.add(new Label("807.. odprawy pośmiertne:"), 0, 4);
        gridAccountCriteria.add(hasDeathBenefitsAccountsCheckBox, 1, 4);

        gridAccountCriteria.add(new Label("...95 zwolnieni:"), 0, 5);
        gridAccountCriteria.add(hasExemptFromFeesAccountsCheckBox, 1, 5);

        gridAccountCriteria.add(new Label("...97 rezygnacja:"), 0, 6);
        gridAccountCriteria.add(hasResignedAccountsCheckBox, 1, 6);

        gridAccountCriteria.add(new Label("...98 skreśleni:"), 0, 7);
        gridAccountCriteria.add(hasRemovedAccountsCheckBox, 1, 7);

        gridAccountCriteria.add(new Label("...99 zmarli:"), 0, 8);
        gridAccountCriteria.add(hasDeceasedAccountsCheckBox, 1, 8);

        gridAccountCriteria.getChildren()
                .stream()
                .filter(node -> node instanceof CheckBox)
                .map(CheckBox.class::cast)
                .forEach(maybeCheckBox -> {
                    maybeCheckBox.selectedProperty().addListener(a -> validateSaveButton(saveButton));

                });

        return gridAccountCriteria;
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        final boolean enableSaveButton =
            Objects.nonNull(unitNumberCombo.getValue()) ||
            !hasDocumentsCheckBox.isIndeterminate() ||
            Objects.nonNull(personStatusCombo.getValue()) ||
            Objects.nonNull(personGenderCombo.getValue()) ||
            isAnyAccountSelected();

        saveButton.setDisable(!enableSaveButton);
    }

    private boolean isAnyAccountSelected() {
        return hasFeesAccountsCheckBox.isSelected() ||
                hasLoansAccountsCheckBox.isSelected() ||
                hasPaydayLoansAccountsCheckBox.isSelected() ||
                hasDeathBenefitsAccountsCheckBox.isSelected() ||
                hasExemptFromFeesAccountsCheckBox.isSelected() ||
                hasResignedAccountsCheckBox.isSelected() ||
                hasRemovedAccountsCheckBox.isSelected() ||
                hasDeceasedAccountsCheckBox.isSelected();
    }

    private Set<Account.AccountType> getSelectedAccountTypes() {
        final Set<Account.AccountType> accountTypes = new HashSet<>();
        if (hasFeesAccountsCheckBox.isSelected()) {
            accountTypes.add(Account.AccountType.FEES);
        }
        if (hasLoansAccountsCheckBox.isSelected()) {
            accountTypes.add(Account.AccountType.LOANS);
        }
        if (hasPaydayLoansAccountsCheckBox.isSelected()) {
            accountTypes.add(Account.AccountType.PAYDAY_LOANS);
        }
        if (hasDeathBenefitsAccountsCheckBox.isSelected()) {
            accountTypes.add(Account.AccountType.DEATH_BENEFITS);
        }
        return accountTypes;
    }

    private Set<String> getSelectedAccountUnitNumbers() {
        final Set<String> unitNumbers = new HashSet<>();
        if (hasExemptFromFeesAccountsCheckBox.isSelected()) {
            unitNumbers.add(Unit.EXEMPT_FROM_FEES_UNIT_NUMBER);
        }
        if (hasResignedAccountsCheckBox.isSelected()) {
            unitNumbers.add(Unit.RESIGNED_UNIT_NUMBER);
        }
        if (hasRemovedAccountsCheckBox.isSelected()) {
            unitNumbers.add(Unit.REMOVED_UNIT_NUMBER);
        }
        if (hasDeceasedAccountsCheckBox.isSelected()) {
            unitNumbers.add(Unit.DECEASED_UNIT_NUMBER);
        }
        return unitNumbers;
    }

    private void updateLabelOnAttachments(CheckBox hasDocumentsCheckBox) {
        final String txt = hasDocumentsCheckBox.isIndeterminate() ? "Bez znaczenia" :
                hasDocumentsCheckBox.isSelected() ? "Obecne" :
                        "Brak załączników";
        hasDocumentsCheckBox.setText(txt);
    }


}
