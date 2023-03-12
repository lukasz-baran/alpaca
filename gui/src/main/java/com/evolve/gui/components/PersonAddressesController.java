package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-addresses-list.fxml")
@Slf4j
@RequiredArgsConstructor
public class PersonAddressesController extends EditableGuiElement implements Initializable {

    private final StageManager stageManager;
    private final ObservableList<AddressEntry> addresses = FXCollections.observableArrayList();

    private final ObjectProperty<AddressEntry> currentAddressProperty = new SimpleObjectProperty<>(null);

    private final BooleanProperty disabledProperty = new SimpleBooleanProperty(true);

    @FXML TableView<AddressEntry> addressesTable;
    @FXML TableColumn<AddressEntry, String> streetColumn;
    @FXML TableColumn<AddressEntry, String> postalCodeColumn;
    @FXML TableColumn<AddressEntry, String> cityColumn;

    @FXML MenuItem deleteAddress;
    @FXML MenuItem addAddress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        streetColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        postalCodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        cityColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        setPersonAddresses(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data

        deleteAddress.setOnAction(this::handleDelete);
        addAddress.setOnAction(this::handleAdd);

        addressesTable.editableProperty().bind(disabledProperty.not());


//        addressesTable.setRowFactory(tv -> {
//            TableRow<AddressEntry> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (!row.isEmpty())) {
//                    AddressEntry rowData = row.getItem();
//                    log.info("Double click on: " + rowData);
//                }
//            });
//            return row;
//        });
        //This will allow the table to select multiple rows at once
        //addressesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        this.addressesTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> this.currentAddressProperty.set(newSelection));

        this.currentAddressProperty
                .addListener((obs, oldEntry, newEntry) -> {
                    if (newEntry == null) {
                        addressesTable.getSelectionModel().clearSelection();
                    } else {
                        AddressEntry selectedPerson = addressesTable.getSelectionModel().getSelectedItem();
                        if (selectedPerson != newEntry) {
                            addressesTable.getSelectionModel().select(newEntry);
                        }
                    }
                });

        deleteAddress.disableProperty().bind(disabledProperty);
        addAddress.disableProperty().bind(disabledProperty);
    }

    public void setPersonAddresses(List<Person.PersonAddress> personAddresses) {
        addresses.clear();
        emptyIfNull(personAddresses)
                .forEach(address -> addresses.add(new AddressEntry(address)));

        addressesTable.setItems(addresses);
    }

    @Override
    protected boolean isEditable() {
        return true;
    }

    @Override
    public boolean startEditing() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        disabledProperty.set(!editable);
    }

    void handleAdd(ActionEvent actionEvent) {
        addressesTable.getItems().add(new AddressEntry("", "", ""));
    }

    void handleDelete(ActionEvent event) {
        final boolean result = stageManager.displayConfirmation("Usunąć zaznaczony adres?");
        if (!result) {
            return;
        }

        final AddressEntry selected = addressesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            log.info("Removing address {}", selected);
            addresses.remove(selected);
            log.info("done");
        }
    }

    /**
     * This method will allow the user to double click on a cell and update
     * the street value.
     */
    public void changeStreetCellEvent(TableColumn.CellEditEvent<AddressEntry, String> event) {
        AddressEntry addressEntry = event.getRowValue();
        addressEntry.setStreet(event.getNewValue());
    }

    public void changePostalCodeCellEvent(TableColumn.CellEditEvent<AddressEntry, String> event) {
        AddressEntry addressEntry = event.getRowValue();
        addressEntry.setPostalCode(event.getNewValue());
    }

    public void changeCityCellEvent(TableColumn.CellEditEvent<AddressEntry, String> event) {
        AddressEntry addressEntry = event.getRowValue();
        addressEntry.setCity(event.getNewValue());
    }

    public List<Person.PersonAddress> getAddresses() {
        return addresses.stream()
                .map(entry -> new Person.PersonAddress(entry.getStreet(), entry.getPostalCode(), entry.getCity(),
                        Person.AddressType.HOME))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class AddressEntry {
        private String street;
        private String postalCode;
        private String city;

        public AddressEntry(Person.PersonAddress personAddress) {
            this(personAddress.getStreet(),
                    personAddress.getPostalCode(), personAddress.getCity());
        }
    }

}
