package com.evolve.gui.components;

import com.evolve.domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-addresses-list.fxml")
@Slf4j
public class PersonAddressesController implements Initializable {
    private final ObservableList<AddressEntry> addresses = FXCollections.observableArrayList();
    @FXML TableView<AddressEntry> addressesTable;
    @FXML TableColumn<AddressEntry, String> streetColumn;
    @FXML TableColumn<AddressEntry, String> postalCodeColumn;
    @FXML TableColumn<AddressEntry, String> cityColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        streetColumn.setCellValueFactory(new PropertyValueFactory<>("street"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        setPersonAddresses(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data
    }

    public void setPersonAddresses(List<Person.PersonAddress> personAddresses) {
        addresses.clear();
        emptyIfNull(personAddresses)
                .forEach(address -> addresses.add(new AddressEntry(address)));

        addressesTable.setItems(addresses);
    }

    @AllArgsConstructor
    @Getter
    public static class AddressEntry {
        private String street;
        private String postalCode;
        private String city;

        public AddressEntry(Person.PersonAddress personAddress) {
            this(personAddress.getStreet(), personAddress.getPostalCode(), personAddress.getCity());
        }
    }

}
