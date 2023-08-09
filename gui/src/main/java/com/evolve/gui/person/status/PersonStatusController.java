package com.evolve.gui.person.status;

import com.evolve.domain.Person;
import com.evolve.domain.PersonStatus;
import com.evolve.domain.PersonStatusChange;
import com.evolve.domain.PersonStatusDetails;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class PersonStatusController extends EditableGuiElement implements Initializable {
    private final StageManager stageManager;
    private final ObjectProperty<PersonStatus> personStatusObjectProperty = new SimpleObjectProperty<>();
    @FXML ComboBox<PersonStatus> personStatusCombo;

    private final ObservableList<PersonHistoryStatusEntry> statusChanges = FXCollections.observableArrayList();

    @FXML TableView<PersonHistoryStatusEntry> statusHistoryTable;
    @FXML TableColumn<PersonHistoryStatusEntry, PersonStatusChange.EventType> statusColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, LocalDate> whenColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, String> originalValueColumn;

    @FXML MenuItem addNewStatus;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        personStatusCombo.getItems().addAll(PersonStatus.values());
        personStatusCombo.valueProperty().bindBidirectional(personStatusObjectProperty);
        personStatusCombo.setDisable(true);
        personStatusCombo.getSelectionModel().select(null);

        addNewStatus.disableProperty().bind(disabledProperty);
        addNewStatus.setOnAction(this::addNewStatus);

        // table
        statusHistoryTable.editableProperty().bind(disabledProperty.not());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        whenColumn.setCellValueFactory(new PropertyValueFactory<>("when"));
        originalValueColumn.setCellValueFactory(new PropertyValueFactory<>("originalValue"));
        statusHistoryTable.setRowFactory(tableView -> {
            final TableRow<PersonHistoryStatusEntry> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            // Dodaj
            final MenuItem newStatusMenuItem = new MenuItem("Nowy");
            newStatusMenuItem.setOnAction(this::addNewStatus);
            newStatusMenuItem.disableProperty().bind(disabledProperty);

            // Edytuj
            final MenuItem editStatusMenuItem = new MenuItem("Edytuj");
            editStatusMenuItem.setOnAction(event -> {
                new PersonStatusEditDialog(row.getItem().getPersonStatusChange())
                        .showDialog(stageManager.getWindow())
                        .ifPresent(personStatusChange -> {
                            row.getItem().setPersonStatusChange(personStatusChange);
                            tableView.refresh();
                        });
            });
            editStatusMenuItem.disableProperty().bind(disabledProperty);

            // Usuń
            final MenuItem removeStatusMenuItem = new MenuItem("Usuń");
            removeStatusMenuItem.setOnAction(event -> {
                row.getItem().getEventType();
                if (stageManager.displayConfirmation("Usunąć status?")) {
                    statusChanges.remove(row.getItem());
                    tableView.refresh();
                }
            });
            removeStatusMenuItem.disableProperty().bind(disabledProperty);

            contextMenu.getItems().addAll(newStatusMenuItem, editStatusMenuItem, removeStatusMenuItem);

            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));

            return row;
        });
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

    private void addNewStatus(ActionEvent actionEvent) {
        PersonStatusEditDialog.newStatus().showDialog(stageManager.getWindow())
                .ifPresent(personStatus -> {
                    statusChanges.add(new PersonHistoryStatusEntry(personStatus));
                    statusHistoryTable.refresh();
                });
    }

}
