package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.importing.event.DbfImportCompletedEvent;
import com.evolve.services.PersonsService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("main-table.fxml")
@RequiredArgsConstructor
@Slf4j
public class MainTableController implements Initializable, ApplicationListener<PersonEditionFinishedEvent> {

    private final BooleanProperty disabledProperty = new SimpleBooleanProperty(false);
    private final PersonListModel personListModel;
    private final PersonsService personsService;
    private final StageManager stageManager;

    @FXML AnchorPane personTableAnchorPane;

    @FXML TableView<PersonModel> personTable;

    @FXML TableColumn<PersonModel, String> idColumn;
    @FXML TableColumn<PersonModel, String> firstNameColumn;
    @FXML TableColumn<PersonModel, String> lastNameColumn;
    @FXML TableColumn<PersonModel, LocalDate> dobColumn;
    @FXML TableColumn<PersonModel, String> statusColumn;
    @FXML TableColumn<PersonModel, Long> registryNumberColumn;

    @FXML TextField filterField;
    @FXML Button btnClearFilter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTable("id", true);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        registryNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registryNumber"));

        personTableAnchorPane.disableProperty().bind(disabledProperty);

        btnClearFilter.setOnAction(event -> filterField.clear());

        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            personListModel.getCurrentPersonProperty().setValue(newValue);
        });
    }

    void populateTable(String sortBy, boolean upDown) {
        final List<PersonListView> persons = personsService.fetchList(
                PersonLookupCriteria.builder().sortBy(sortBy).upDown(upDown).build());

        // TODO set pagination
        final int numberOfPersons = persons.size();
        log.info("total person number {}", numberOfPersons);

        personListModel.feed(persons);

        FilteredList<PersonModel> filteredData = personListModel.getFilteredList();

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                String firstName = StringUtils.trimToEmpty(person.getFirstName()).toLowerCase();
                if (firstName.contains(lowerCaseFilter)) {
                    return true;
                }

                if (StringUtils.trimToEmpty(person.getLastName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                if (StringUtils.trimToEmpty(person.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<PersonModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(personTable.comparatorProperty());

        personTable.setItems(sortedData);
    }

    @EventListener
    public void handleContextStart(DbfImportCompletedEvent importCompleted) {
        log.info("import: " + importCompleted.getMessage());
        stageManager.displayInformation(importCompleted.getMessage());

        populateTable("id", true);
    }

    @Override
    public void onApplicationEvent(PersonEditionFinishedEvent event) {
        event.getEditedPerson().ifPresent(updatedPerson -> {
            log.info("Edition successful");
            // update person table with person data from event
            personListModel.updatePerson(updatedPerson);
            personTable.refresh();
        });

        disableControls(false);

    }

    public void personInserted(Person newPerson) {
        final PersonModel personModel = personListModel.insertPerson(newPerson);
        personTable.refresh();
        personTable.getSelectionModel().select(personModel);
        personTable.scrollTo(personModel);
    }

    public void disableControls(boolean disable) {
        disabledProperty.setValue(disable);
    }
}