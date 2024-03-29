package com.evolve.gui.person.status;

import com.evolve.alpaca.util.TableViewResizer;
import com.evolve.domain.Person;
import com.evolve.domain.PersonStatusChange;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("person-status.fxml")
@Slf4j
@RequiredArgsConstructor
public class PersonStatusController extends EditableGuiElement implements Initializable {
    private final StageManager stageManager;
    private final ObservableList<PersonHistoryStatusEntry> statusChanges = FXCollections.observableArrayList();

    @FXML TableView<PersonHistoryStatusEntry> statusHistoryTable;
    @FXML TableColumn<PersonHistoryStatusEntry, PersonStatusChange.EventType> statusColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, LocalDate> whenColumn;
    @FXML TableColumn<PersonHistoryStatusEntry, String> originalValueColumn;

    @FXML MenuItem addNewStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addNewStatus.disableProperty().bind(disabledProperty);
        addNewStatus.setOnAction(this::addNewStatus);

        statusHistoryTable.setItems(statusChanges);

        // table
        statusHistoryTable.editableProperty().bind(disabledProperty.not());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        whenColumn.setCellValueFactory(new PropertyValueFactory<>("when"));
        originalValueColumn.setCellValueFactory(new PropertyValueFactory<>("originalValue"));

        TableViewResizer.resizeTable(statusHistoryTable);
        statusHistoryTable.setRowFactory(tableView -> {
            final TableRow<PersonHistoryStatusEntry> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem newStatusMenuItem = new MenuItem("Nowy");
            newStatusMenuItem.setOnAction(this::addNewStatus);
            newStatusMenuItem.disableProperty().bind(disabledProperty);

            final MenuItem editStatusMenuItem = new MenuItem("Edytuj");
            editStatusMenuItem.setOnAction(event -> editStatusNumber(tableView, row));
            editStatusMenuItem.disableProperty().bind(disabledProperty);

            row.setOnMouseClicked(event -> {
                if (disabledProperty.get()) {
                    return;
                }

                if (event.getClickCount() == 2) {
                    if (row.isEmpty()) {
                        addNewStatus(event);
                    } else {
                        editStatusNumber(tableView, row);
                    }
                }
            });

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

    }

    public void setPerson(Person person) {
        statusChanges.clear();
        emptyIfNull(person.getStatusChanges())
                .forEach(status -> statusChanges.add(new PersonHistoryStatusEntry(status)));
    }

    private void addNewStatus(Event actionEvent) {
        PersonStatusEditDialog.newStatus(getStatusChanges()).showDialog(stageManager.getWindow())
                .ifPresent(personStatus -> {
                    statusChanges.add(new PersonHistoryStatusEntry(personStatus));
                    statusHistoryTable.refresh();
                });
    }

    private void editStatusNumber(TableView<PersonHistoryStatusEntry> tableView, TableRow<PersonHistoryStatusEntry> row) {
        PersonStatusEditDialog.editStatus(row.getItem().getPersonStatusChange()).showDialog(stageManager.getWindow())
                .ifPresent(personStatusChange -> {
                    row.getItem().setPersonStatusChange(personStatusChange);
                    tableView.refresh();
                });
    }

    public List<PersonStatusChange> getStatusChanges() {
        return statusChanges.stream()
                .map(PersonHistoryStatusEntry::getPersonStatusChange)
                .collect(Collectors.toList());
    }

}
