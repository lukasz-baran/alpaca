package com.evolve.gui.person.list.search;

import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.search.PersonSearchCriteria;
import com.evolve.alpaca.unit.Unit;
import com.evolve.alpaca.unit.services.UnitsService;
import com.evolve.gui.DialogWindow;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class SearchPersonDialog extends DialogWindow<PersonSearchCriteria> {

    private final PersonCriteriaGrid searchPersonGrid;

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
        this.searchPersonGrid = new PersonCriteriaGrid(unitsService);
    }

    @Override
    public Optional<PersonSearchCriteria> showDialog(Window window) {
        final Dialog<PersonSearchCriteria> dialog = createDialog(window);
        findSubmitButton(dialog)
                .setText("Szukaj");
        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        final HBox mainHBox = new HBox();
        final GridPane gridPersonCriteria = searchPersonGrid.createPersonSearchCriteria(createGridPane(),
                () -> validateSaveButton(saveButton));

        final GridPane gridAccountCriteria = createAccountsSearchCriteria(saveButton);
        mainHBox.getChildren().addAll(gridPersonCriteria, gridAccountCriteria);

        dialog.getDialogPane().setContent(mainHBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new PersonSearchCriteria(
                        searchPersonGrid.getUnitNumber(),
                        searchPersonGrid.hasDocuments(),
                        searchPersonGrid.getPersonStatus(),
                        searchPersonGrid.getPersonGender(),
                        getSelectedAccountTypes(),
                        getSelectedAccountUnitNumbers(),
                        searchPersonGrid.isRetired(),
                        searchPersonGrid.isExemptFromFees());
            }
            return null;
        });

        return dialog.showAndWait();
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
        final boolean enableSaveButton = searchPersonGrid.isAnyPersonFilterChanged() ||
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

}
