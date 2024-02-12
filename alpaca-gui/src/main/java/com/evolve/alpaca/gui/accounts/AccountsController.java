package com.evolve.alpaca.gui.accounts;

import com.evolve.alpaca.account.Account;
import com.evolve.alpaca.account.AccountLookupCriteria;
import com.evolve.alpaca.account.FindAccount;
import com.evolve.gui.StageManager;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Getter
@Component
@FxmlView("accounts-dialog.fxml")
@RequiredArgsConstructor
@Slf4j
public class AccountsController implements Initializable {
    public static final String ACCOUNTS_DIALOG_TITLE = "Plan kont";

    //private final ObservableList<AccountEntry> accountEntries = FXCollections.observableArrayList();
    private final AccountsListModel accountListModel;
    private final StageManager stageManager;
    private final FindAccount findAccount;
    private Stage stage;

    @FXML HBox accountsDialog;
    @FXML TableView<AccountEntry> accountsTable;
    @FXML TableColumn<AccountEntry, String> accountIdColumn;
    @FXML TableColumn<AccountEntry, String> unitNumberColumn;
    @FXML TableColumn<AccountEntry, String> personIdColumn;
    @FXML TableColumn<AccountEntry, String> accountNameColumn;

    @FXML AnchorPane autoCompletePane;
    @FXML
    TextField filterField;


    @FXML Button btnClose;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(accountsDialog));
        stage.setTitle(ACCOUNTS_DIALOG_TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        //stage.setResizable(false);

        accountIdColumn.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        personIdColumn.setCellValueFactory(new PropertyValueFactory<>("personId"));
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));

    }

    private void populateTable(AccountLookupCriteria criteria) {
        final List<Account> accounts = findAccount.fetch(criteria);

        log.info("total number of accounts {}", accounts.size());

        final FilteredList<AccountEntry> filteredData = accountListModel.feed(accounts);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(accountEntry -> accountEntry.matches(newValue));
            //refreshNumberOfItems();
        });

        final SortedList<AccountEntry> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(accountsTable.comparatorProperty());

        accountsTable.setItems(sortedData);
        //refreshNumberOfItems();
    }

    public void show() {
        stage.show();
        populateTable(AccountLookupCriteria.ALL);
    }

    @FXML
    void onClose(ActionEvent actionEvent) {
        stage.close();
    }

}
