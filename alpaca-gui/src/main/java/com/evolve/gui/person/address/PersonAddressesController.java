package com.evolve.gui.person.address;

import com.evolve.alpaca.util.TableViewResizer;
import com.evolve.domain.Address;
import com.evolve.domain.Person;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-addresses-list.fxml")
@Slf4j
@RequiredArgsConstructor
public class PersonAddressesController extends EditableGuiElement implements Initializable {

    private final StageManager stageManager;
    private final ObservableList<AddressEntry> list = FXCollections.observableArrayList();

    @FXML TableView<AddressEntry> addressesTable;
    @FXML TableColumn<AddressEntry, Image> addressTypeColumn;
    @FXML TableColumn<AddressEntry, String> streetColumn;
    @FXML TableColumn<AddressEntry, String> postalCodeColumn;
    @FXML TableColumn<AddressEntry, String> cityColumn;
    @FXML TableColumn<AddressEntry, String> commentColumn;

    @FXML MenuItem addAddress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addressTypeColumn.setCellValueFactory(new PropertyValueFactory<>("imageType"));
        addressTypeColumn.setCellFactory(item -> new AddressTypeIconTableCell());
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        setPersonAddresses(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data

        addAddress.disableProperty().bind(disabledProperty);
        addAddress.setOnAction(this::addPersonAddress);

        TableViewResizer.resizeTable(addressesTable);
        addressesTable.editableProperty().bind(disabledProperty.not());
        addressesTable.setRowFactory(tableView -> {
            final TableRow<AddressEntry> row = new PersonAddressRow();
            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem copyAddress = new MenuItem("Kopiuj");
            copyAddress.setOnAction(event -> Optional.ofNullable(row.getItem())
                    .map(PersonAddressesController::concatenatedAddressString)
                    .ifPresent(text -> {
                        final ClipboardContent clipboardContent = new ClipboardContent();
                        clipboardContent.putString(text);
                        Clipboard.getSystemClipboard().setContent(clipboardContent);
            }));

            final MenuItem editMenuItem = new MenuItem("Edytuj");
            editMenuItem.setOnAction(event -> editPersonAddress(tableView, row));
            editMenuItem.disableProperty().bind(disabledProperty);

            row.setOnMouseClicked(event -> {
                if (disabledProperty.get()) {
                    return;
                }

                if (event.getClickCount() == 2) {
                    if (row.isEmpty()) {
                        addPersonAddress(event);
                    } else {
                        editPersonAddress(tableView, row);
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



    public void setPersonAddresses(List<Person.PersonAddress> personAddresses) {
        list.clear();
        emptyIfNull(personAddresses)
                .forEach(address -> list.add(new AddressEntry(address)));

        addressesTable.setItems(list);
    }

    private void addPersonAddress(Event actionEvent) {
         new PersonAddressDialog(null).showDialog(stageManager.getWindow())
             .ifPresent(person -> {
                list.add(new AddressEntry(person));
                addressesTable.refresh();
            });
    }

    private void editPersonAddress(TableView<AddressEntry> tableView, TableRow<AddressEntry> row) {
        new PersonAddressDialog(row.getItem().getPersonAddress()).showDialog(stageManager.getWindow())
                .ifPresent(person -> {
                    row.getItem().setPersonAddress(person);
                    tableView.refresh();
                });
    }

    public List<Person.PersonAddress> getPersonAddresses() {
        return list.stream()
                .map(AddressEntry::getPersonAddress)
                .collect(Collectors.toList());
    }

    private static String concatenatedAddressString(AddressEntry addressEntry) {
        final Person.PersonAddress address = addressEntry.getPersonAddress();
        return Address.toConcatenatedAddress(address);
    }

    public static class PersonAddressRow extends TableRow<AddressEntry> {
        private final Tooltip tooltip = new Tooltip();

        @Override
        public void updateItem(AddressEntry addressEntry, boolean empty) {
            super.updateItem(addressEntry, empty);
            if (addressEntry == null) {
                setTooltip(null);
            } else {
                tooltip.setText(PersonAddressesController.concatenatedAddressString(addressEntry));
                setTooltip(tooltip);
            }
        }
    }

    static class AddressTypeIconTableCell extends TableCell<AddressEntry, Image> {
        private final ImageView imageView = new ImageView();

        public AddressTypeIconTableCell() {
            imageView.setFitHeight(12);
            imageView.setFitWidth(12);
        }

        @Override
        protected void updateItem(Image image, boolean empty) {
            super.updateItem(image, empty);

            if (image == null || empty) {
                imageView.setImage(null);
                setGraphic(null);
            } else {
                imageView.setImage(image);
                setGraphic(imageView);
            }
        }
    }

}
