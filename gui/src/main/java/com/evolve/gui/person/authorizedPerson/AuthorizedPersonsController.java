package com.evolve.gui.person.authorizedPerson;

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

@Component
@FxmlView("authorized-persons-list.fxml")
@Slf4j
@RequiredArgsConstructor
public class AuthorizedPersonsController extends EditableGuiElement implements Initializable {
    private final ObservableList<AuthorizedPersonEntry> list = FXCollections.observableArrayList();
    private final StageManager stageManager;

    @FXML TableView<AuthorizedPersonEntry> authorizedPersonsTable;
    @FXML TableColumn<AuthorizedPersonEntry, String> authorizedFirstNameColumn;
    @FXML TableColumn<AuthorizedPersonEntry, String> authorizedLastNameColumn;
    @FXML TableColumn<AuthorizedPersonEntry, String> relationColumn;

    @FXML MenuItem addAuthorizedPerson;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authorizedFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        authorizedLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        relationColumn.setCellValueFactory(new PropertyValueFactory<>("relation"));

        setAuthorizedPersons(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data

        authorizedPersonsTable.editableProperty().bind(disabledProperty.not());

        addAuthorizedPerson.disableProperty().bind(disabledProperty);

        addAuthorizedPerson.setOnAction(this::addAuthorizedPerson);

        authorizedPersonsTable.setRowFactory(tableView -> {
                final TableRow<AuthorizedPersonEntry> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();

                final MenuItem editMenuItem = new MenuItem("Edytuj");
                editMenuItem.setOnAction(event -> editAuthorizedPerson(tableView, row));
                editMenuItem.disableProperty().bind(disabledProperty);

                row.setOnMouseClicked(event -> {
                    if (disabledProperty.get()) {
                        return;
                    }

                    if (event.getClickCount() == 2) {
                        if (row.isEmpty()) {
                            addAuthorizedPerson(event);
                        } else {
                            editAuthorizedPerson(tableView, row);
                        }
                    }
                });

                final MenuItem removeMenuItem = new MenuItem("Usuń");
                removeMenuItem.setOnAction(event -> {
                    list.remove(row.getItem());
                    tableView.refresh();
                });
                removeMenuItem.disableProperty().bind(disabledProperty);

                contextMenu.getItems().add(editMenuItem);
                contextMenu.getItems().add(removeMenuItem);

                // Set context menu on row, but use a binding to make it only show for non-empty rows:
                row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                        .otherwise(contextMenu));
                return row;
            });
    }

    private void addAuthorizedPerson(Event actionEvent) {
        new AuthorizedPersonDialog(null)
            .showDialog(stageManager.getWindow())
            .ifPresent(person -> {
                list.add(new AuthorizedPersonEntry(person));
                authorizedPersonsTable.refresh();
            });
    }

    private void editAuthorizedPerson(TableView<AuthorizedPersonEntry> tableView, TableRow<AuthorizedPersonEntry> row) {
        new AuthorizedPersonDialog(row.getItem().getAuthorizedPerson())
                .showDialog(stageManager.getWindow())
                .ifPresent(person -> {
                    row.getItem().setAuthorizedPerson(person);
                    tableView.refresh();
                });
    }

    public void setAuthorizedPersons(List<Person.AuthorizedPerson> authorizedPersons) {
        list.clear();
        emptyIfNull(authorizedPersons)
                .forEach(authorizedPerson ->
                        list.add(new AuthorizedPersonEntry(authorizedPerson)));

        authorizedPersonsTable.setItems(list);
    }

    public List<Person.AuthorizedPerson> getAuthorizedPersons() {
        return list.stream()
                .map(AuthorizedPersonEntry::getAuthorizedPerson)
                .collect(Collectors.toList());
    }

}
