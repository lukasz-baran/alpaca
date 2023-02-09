package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.PersonStatusChange;
import com.evolve.domain.PersonStatusDetails;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-status.fxml")
@Slf4j
public class PersonStatusController implements Initializable {
    private final ObjectProperty<PersonStatus> personStatusObjectProperty = new SimpleObjectProperty<>();
    @FXML ComboBox<PersonStatus> personStatusCombo;

    private final ObservableList<PersonHistoryStatusEntry> statusChanges = FXCollections.observableArrayList();

    @FXML TableView<PersonHistoryStatusEntry> statusHistoryTable;
    @FXML TableColumn<PersonHistoryStatusEntry, PersonStatusChange.EventType> statusColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, LocalDate> whenColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, String> originalValueColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personStatusCombo.getItems().addAll(PersonStatus.values());
        personStatusCombo.valueProperty().bindBidirectional(personStatusObjectProperty);
        personStatusCombo.setDisable(true);
        personStatusCombo.getSelectionModel().select(null);


        statusColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        whenColumn.setCellValueFactory(new PropertyValueFactory<>("when"));
        originalValueColumn.setCellValueFactory(new PropertyValueFactory<>("originalValue"));

        setPersonStatusHistory(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data
    }


    public void setPerson(Person person) {
        setPersonStatus(person.getStatus());
        setPersonStatusHistory(person.getStatusChanges());
    }

    void setPersonStatus(PersonStatusDetails personStatus) {
        log.info("Setting person status: {}", personStatus);
        personStatusObjectProperty.setValue(personStatus != null ? personStatus.getStatus() : null);
    }

    void setPersonStatusHistory(final List<PersonStatusChange> personStatusChanges) {
        statusChanges.clear();
        emptyIfNull(personStatusChanges)
                .forEach(address -> statusChanges.add(new PersonHistoryStatusEntry(address)));

        statusHistoryTable.setItems(statusChanges);
    }

    @AllArgsConstructor
    @Getter
    public static class PersonHistoryStatusEntry {
        private PersonStatusChange.EventType eventType;
        private LocalDate when;
        private String originalValue;

        public PersonHistoryStatusEntry(PersonStatusChange personStatusChange) {
            this(personStatusChange.getEventType(), personStatusChange.getWhen(), personStatusChange.getOriginalValue());
        }
    }

}
