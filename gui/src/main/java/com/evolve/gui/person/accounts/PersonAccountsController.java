package com.evolve.gui.person.accounts;

import com.evolve.domain.Account;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import com.evolve.services.AccountsService;
import com.evolve.services.UnitsService;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.*;
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
    private final AccountsService accountsService;
    private final AccountTooltipService accountTooltipService;

    private final ObservableList<AccountEntry> data = FXCollections.observableArrayList();

    @FXML TableColumn<AccountEntry, String> accountIdColumn;
    @FXML TableColumn<AccountEntry, Account.AccountType> accountTypeColumn;
    @FXML TableColumn<AccountEntry, String> accountNameColumn;
    @FXML TableView<AccountEntry> accountsTable;

    public PersonAccountsController(PersonListModel personListModel, AccountsService accountsService, UnitsService unitsService) {
        this.personListModel = personListModel;
        this.accountsService = accountsService;
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
                setTooltip(new Tooltip(accountTooltipService.forAccountNumber(accountId)));
            }
        });

        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));



        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> populatePersonAccounts(newUser));
    }

    public void populatePersonAccounts(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return;
        }

        final List<Account> accounts = accountsService.findByPersonId(personModel.getId());
        log.info("Person accounts: {}", accounts);

        data.clear();

        accounts.forEach(account -> data.add(AccountEntry.of(account)));

        accountsTable.setItems(data);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AccountEntry {
        private String accountId;
        private Account.AccountType accountType;
        private String accountName;

        public static AccountEntry of(Account account) {
            return new AccountEntry(account.getAccountId(), account.getAccountType(), account.getAccountName());
        }
    }
}
