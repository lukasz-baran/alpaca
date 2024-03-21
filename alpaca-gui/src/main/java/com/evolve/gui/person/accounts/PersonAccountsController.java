package com.evolve.gui.person.accounts;

import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.account.FindAccount;
import com.evolve.alpaca.unit.services.UnitsService;
import com.evolve.gui.StageManager;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("person-accounts.fxml")
@Slf4j
public class PersonAccountsController implements Initializable {
    private final PersonListModel personListModel;
    private final FindAccount findAccount;
    private final AccountTooltipService accountTooltipService;

    @Getter
    private final ObservableList<AccountEntry> accountsList = FXCollections.observableArrayList();

    @FXML TableColumn<AccountEntry, String> accountIdColumn;
    @FXML TableColumn<AccountEntry, Account.AccountType> accountTypeColumn;
    @FXML TableColumn<AccountEntry, String> unitNumberColumn;
    @FXML TableColumn<AccountEntry, String> accountNameColumn;
    @FXML TableView<AccountEntry> accountsTable;

    public PersonAccountsController(PersonListModel personListModel, FindAccount findAccount, UnitsService unitsService) {
        this.personListModel = personListModel;
        this.findAccount = findAccount;
        this.accountTooltipService = new AccountTooltipService(unitsService);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountIdColumn.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        accountIdColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String accountId, boolean empty) {
                super.updateItem(accountId, empty);
                setText(accountId);

                accountTooltipService.forAccountNumber(accountId)
                        .map(StageManager::newTooltip)
                        .ifPresent(this::setTooltip);
            }
        });

        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));



        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> populatePersonAccounts(newUser));
    }

    public List<Account> populatePersonAccounts(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return List.of();
        }

        final List<Account> accounts = findAccount.findByPersonId(personModel.getId());
        log.info("Person accounts: {}", accounts);

        accountsList.clear();

        accountsList.addAll(accounts.stream().map(AccountEntry::of).toList());

        accountsTable.setItems(accountsList);
        accountsTable.refresh(); // refresh is called to clear tooltips attached to emptied cells
        return accounts;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AccountEntry {
        private String accountId;
        private Account.AccountType accountType;
        private String unitNumber;
        private String accountName;

        public static AccountEntry of(Account account) {
            return new AccountEntry(account.getAccountId(), account.getAccountType(),
                    account.getUnitNumber(), account.getAccountName());
        }
    }
}
