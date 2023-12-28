package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
@FxmlView("person-bank-accounts.fxml")
@Slf4j
@RequiredArgsConstructor
public class PersonBankAccountsController extends EditableGuiElement implements Initializable {
    private final StageManager stageManager;

    private final ObservableList<BankAccountEntry> list = FXCollections.observableArrayList();

    @FXML TableView<BankAccountEntry> personBankAccountsTable;
    @FXML TableColumn<BankAccountEntry, String> bankAccountNumberColumn;

    public MenuItem addNewBankAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bankAccountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        personBankAccountsTable.setItems(list);

        addNewBankAccount.disableProperty().bind(disabledProperty);
        addNewBankAccount.setOnAction(this::addNewBankAccount);

        personBankAccountsTable.editableProperty().bind(disabledProperty.not());
        personBankAccountsTable.setRowFactory(tableView -> {
            final TableRow<BankAccountEntry> row = new TableRow<>();

            final ContextMenu contextMenu = createContextMenu(tableView, row);

            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));
            return row;
        });
    }

    public void setPersonBankAccounts(List<BankAccount> bankAccounts) {
        list.clear();
        emptyIfNull(bankAccounts)
                .forEach(bankAccount -> list.add(new BankAccountEntry(bankAccount.getNumber())));

        personBankAccountsTable.setItems(list);
    }

    private ContextMenu createContextMenu(TableView<BankAccountEntry> tableView, TableRow<BankAccountEntry> row) {
        final ContextMenu contextMenu = new ContextMenu();

        final MenuItem copyNumber = new MenuItem("Kopiuj");
        copyNumber.setOnAction(event -> {
            final String text = trimToEmpty(row.getItem().getNumber());

            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(text);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        });

        final MenuItem editMenuItem = new MenuItem("Edytuj");
        editMenuItem.setOnAction(event -> editBankAccount(tableView, row));
        editMenuItem.disableProperty().bind(disabledProperty);

        row.setOnMouseClicked(event -> {
            if (disabledProperty.get()) {
                return;
            }

            if (event.getClickCount() == 2) {
                if (row.isEmpty()) {
                    addNewBankAccount(event);
                } else {
                    editBankAccount(tableView, row);
                }
            }
        });

        final MenuItem removeMenuItem = new MenuItem("Usuń");
        removeMenuItem.setOnAction(event -> {
            list.remove(row.getItem());
            tableView.refresh();
        });
        removeMenuItem.disableProperty().bind(disabledProperty);

        contextMenu.getItems().add(copyNumber);
        contextMenu.getItems().add(editMenuItem);
        contextMenu.getItems().add(removeMenuItem);
        return contextMenu;
    }

    private void addNewBankAccount(Event actionEvent) {
        new BankAccountDialog(null).showDialog(stageManager.getWindow())
                .ifPresent(number -> {
                    System.out.println(number);
                    list.add(number);
                    personBankAccountsTable.refresh();
                });
    }

    private void editBankAccount(TableView<BankAccountEntry> tableView, TableRow<BankAccountEntry> row) {
        new BankAccountDialog(row.getItem()).showDialog(stageManager.getWindow())
                .ifPresent(item -> {
                    System.out.println(item);
                    row.getItem().setNumber(item.getNumber());
                    tableView.refresh();
                });
    }

    public List<BankAccount> getAccounts() {
        return list.stream()
                .map(BankAccountEntry::toBankAccount)
                .collect(Collectors.toList());
    }
}
