package com.evolve.gui.person.phoneNumber;

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
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
@FxmlView("phone-numbers.fxml")
@Slf4j
@RequiredArgsConstructor
public class PhoneNumbersController extends EditableGuiElement implements Initializable {
    private final StageManager stageManager;
    private final ObservableList<PhoneNumberEntry> list = FXCollections.observableArrayList();

    @FXML TableView<PhoneNumberEntry> phoneNumbersTable;
    @FXML TableColumn<PhoneNumberEntry, String> phoneNumber;

    @FXML MenuItem addPhoneNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));

        setPhoneNumbers(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data

        addPhoneNumber.disableProperty().bind(disabledProperty);
        addPhoneNumber.setOnAction(this::addPhoneNumber);

        phoneNumbersTable.editableProperty().bind(disabledProperty.not());
        phoneNumbersTable.setRowFactory(tableView -> {
            final TableRow<PhoneNumberEntry> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem copyAddress = new MenuItem("Kopiuj");
            copyAddress.setOnAction(event -> {
                final String text = trimToEmpty(row.getItem().getNumber());

                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(text);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            });

            final MenuItem editMenuItem = new MenuItem("Edytuj");
            editMenuItem.setOnAction(event -> editPhoneNumber(tableView, row));
            editMenuItem.disableProperty().bind(disabledProperty);

            row.setOnMouseClicked(event -> {
                if (disabledProperty.get()) {
                    return;
                }

                if (event.getClickCount() == 2) {
                    if (row.isEmpty()) {
                        addPhoneNumber(event);
                    } else {
                        editPhoneNumber(tableView, row);
                    }
                }
            });

            final MenuItem removeMenuItem = new MenuItem("Usuń");
            removeMenuItem.setOnAction(event -> {
                list.remove(row.getItem());
                tableView.refresh();
            });
            removeMenuItem.disableProperty().bind(disabledProperty);

            contextMenu.getItems().add(copyAddress);
            contextMenu.getItems().add(editMenuItem);
            contextMenu.getItems().add(removeMenuItem);

            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));
            return row;
        });

    }

    public List<String> getNumbers() {
        return list.stream()
                .map(PhoneNumberEntry::getNumber)
                .collect(Collectors.toList());
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        list.clear();
        emptyIfNull(phoneNumbers)
                .forEach(number -> list.add(new PhoneNumberEntry(number)));

        phoneNumbersTable.setItems(list);
    }


    private void addPhoneNumber(Event actionEvent) {
        new PhoneNumberDialog(null).showDialog(stageManager.getWindow())
                .ifPresent(number -> {
                    list.add(new PhoneNumberEntry(number));
                    phoneNumbersTable.refresh();
                });
    }

    private void editPhoneNumber(TableView<PhoneNumberEntry> tableView, TableRow<PhoneNumberEntry> row) {
        new PhoneNumberDialog(row.getItem().getNumber()).showDialog(stageManager.getWindow())
                .ifPresent(number -> {
                    row.getItem().setNumber(number);
                    tableView.refresh();
                });
    }


}
